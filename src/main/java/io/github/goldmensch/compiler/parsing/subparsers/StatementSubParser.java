package io.github.goldmensch.compiler.parsing.subparsers;

import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.Position;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.parsing.ParserData;
import io.github.goldmensch.compiler.parsing.SubParser;

import java.util.ArrayList;
import java.util.List;

public class StatementSubParser extends SubParser<Statement> {
    public StatementSubParser(ParserData parserData) {
        super(parserData);
    }

    @Override
    public Statement parse() {
        return switch (peek().type()) {
            case RETURN -> returnStmt();
            case IDENTIFIER -> funcCall();
            case IF -> ifStmt();
            case WHILE -> whileStmt();
            case DO -> doWhile();
            case LOOP -> loop();
            case NUMBER -> times();
            case TEST -> test();
            case TRY -> tryStmt();
            case LEFT_BRACE -> block();
            default -> throw expectedError("statement");
        };
    }

    Statement.FuncCall funcCall() {
        consume(TokenType.IDENTIFIER);
        Token identifier = previous();
        consume(TokenType.LEFT_PAREN);
        Token argument = null;
        if (match(TokenType.RED, TokenType.BLUE, TokenType.GREEN, TokenType.YELLOW, TokenType.NUMBER)) {
            argument = previous();
        }
        consume(TokenType.RIGHT_PAREN);
        return new Statement.FuncCall(((String) identifier.literal()), argument, new Position(identifier));
    }

    Statement.Block block() {
        var statements = new ArrayList<Statement>();
        if (match(TokenType.LEFT_BRACE)) {
            while (!check(TokenType.RIGHT_BRACE) && !(nextIsEnd())) {
                statements.add(statement());
            }
            consume(TokenType.RIGHT_BRACE);
        } else {
            throw expectedError(TokenType.LEFT_BRACE);
        }
        return new Statement.Block(statements);
    }

    private Statement returnStmt() {
        Token token = consume(TokenType.RETURN);

        return new Statement.Return(expression(), new Position(token));
    }

    private Statement whileStmt() {
        Token token = consume(TokenType.WHILE);

        return new Statement.While(expression(), block(), new Position(token));
    }

    private Statement ifStmt() {
        Token token = consume(TokenType.IF);
        Expression expression = expression();
        Statement.Block thenBlock = block();

        Statement.Block elseBlock = new Statement.Block(List.of());
        if (match(TokenType.ElSE)) {
            elseBlock = switch (peek().type()) {
                case LEFT_BRACE -> block();
                case IF -> new Statement.Block(List.of(ifStmt()));
                default -> throw expectedError(TokenType.LEFT_BRACE, TokenType.IF);
            };
        }
        return new Statement.If(expression, thenBlock, elseBlock, new Position(token));
    }

    private Statement loop() {
        Token token = consume(TokenType.LOOP);

        return new Statement.Loop(block(), new Position(token));
    }

    private Statement doWhile() {
        Token token = consume(TokenType.DO);

        Statement.Block block = block();
        consume(TokenType.WHILE);
        Expression cond = expression();
        return new Statement.DoWhile(block, cond, new Position(token));
    }

    private Statement times() {
        Token token = consume(TokenType.NUMBER);

        int number = (int) previous().literal();
        if (!match(TokenType.TIMES)) throw expectedError(TokenType.TIMES);
        return new Statement.Times(number, block(), new Position(token));
    }

    private Statement test() {
        Token token = consume(TokenType.TEST);

        String condCallIdentifier = ((String) consume(TokenType.IDENTIFIER).literal());
        consume(TokenType.LEFT_PAREN);
        consume(TokenType.IDENTIFIER);
        if (!previous().lexeme().equals("x")) {
            throw expectedError("x as an argument placeholder in test");
        }
        consume(TokenType.RIGHT_PAREN);

        consume(TokenType.LEFT_BRACE);

        var arguments = new ArrayList<Token>();
        var statements = new ArrayList<Statement.Block>();
        do {
            switch (advance().type()) {
                case UNDERSCORE, NUMBER, RED, GREEN, YELLOW, BLUE -> arguments.add(previous());
                default ->
                        throw expectedError(TokenType.UNDERSCORE, TokenType.NUMBER, TokenType.RED, TokenType.GREEN, TokenType.YELLOW, TokenType.BLUE);
            }

            consume(TokenType.ARROW);

            statements.add(block());
        } while (!match(TokenType.RIGHT_BRACE));
        return new Statement.Test(condCallIdentifier, arguments, statements, new Position(token));
    }

    private Statement tryStmt() {
        Token token = consume(TokenType.TRY);

        consume(TokenType.LEFT_BRACE);
        var conditions = new ArrayList<Expression>();
        var statements = new ArrayList<Statement.Block>();
        do {
            if (match(TokenType.UNDERSCORE)) {
                conditions.add(new Expression.DefaultTryArm(previous().line()));
            } else {
                conditions.add(expression());
            }
            consume(TokenType.ARROW);
            statements.add(block());
        } while (!match(TokenType.RIGHT_BRACE));
        return new Statement.Try(conditions, statements, new Position(token));
    }
}
