package io.github.goldmensch.compiler.parsing.subparsers;

import io.github.goldmensch.compiler.ast.Position;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.parsing.ParserData;
import io.github.goldmensch.compiler.parsing.SubParser;

import java.util.List;

public class TopLevelSubParser extends SubParser<TopLevelConstruct> {
    public TopLevelSubParser(ParserData parserData) {
        super(parserData);
    }

    @Override
    public TopLevelConstruct parse() {
        return switch (peek().type()) {
            case MAIN -> mainTL();
            case FUN -> fun();
            case COND -> cond();
            default -> throw expectedError(TokenType.MAIN, TokenType.FUN, TokenType.COND);
        };
    }

    private TopLevelConstruct mainTL() {
        Token token = consume(TokenType.MAIN);
        Token temp = checkAndConsumeTemp();

        Statement.Block block = parser().statementParser().block();

        return new TopLevelConstruct.Main(wrapTemp(temp, block), new Position(token));
    }

    private TopLevelConstruct fun() {
        Token token = consume(TokenType.FUN);
        Token temp = checkAndConsumeTemp();
        consume(TokenType.IDENTIFIER);

        return new TopLevelConstruct.Function(((String) previous().literal()), wrapTemp(temp, parser().statementParser().block()), new Position(token));
    }

    private TopLevelConstruct cond() {
        Token token = consume(TokenType.COND);
        Token temp = checkAndConsumeTemp();
        consume(TokenType.IDENTIFIER);

        return new TopLevelConstruct.Condition(((String) previous().literal()), wrapTemp(temp, parser().statementParser().block()), new Position(token));
    }

    private Token checkAndConsumeTemp() {
        match(TokenType.FAST, TokenType.SLOW);
        return previous();
    }

    private Statement.Block wrapTemp(Token check, Statement.Block block) {
        Statement stmt = switch (check.type()) {
            case FAST -> new Statement.Fast(block, null);
            case SLOW -> new Statement.Slow(block, null);
            default -> null;
        };
        if (stmt == null) return block;
        return new Statement.Block(List.of(stmt));
    }
}
