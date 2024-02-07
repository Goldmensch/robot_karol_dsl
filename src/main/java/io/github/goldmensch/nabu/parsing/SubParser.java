package io.github.goldmensch.nabu.parsing;

import io.github.goldmensch.nabu.ast.Expression;
import io.github.goldmensch.nabu.ast.Statement;
import io.github.goldmensch.nabu.lexing.Token;
import io.github.goldmensch.nabu.lexing.TokenType;

import java.util.Arrays;

public abstract class SubParser<T> {
    private final ParserData parserData;

    public SubParser(ParserData parserData) {
        this.parserData = parserData;
    }

    public abstract T parse();

    public Expression expression() {
        return parserData.subParsers().expressionParser().parse();
    }

    public Statement statement() {
        return parserData.subParsers().statementParser().parse();
    }

    public ParserData.SubParsers parser() {
        return parserData.subParsers();
    }

    public Token advance() {
        if (!isAtEnd()) parserData.increaseCurrent();
        return previous();
    }

    public boolean check(TokenType type) {
        if (type == TokenType.IDENTIFIER && TokenType.isKeyword(peek().type())) {
            throw error(peek(), "Keyword '%s' cannot be used as an identifier".formatted(peek().lexeme()));
        }
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    public boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    public boolean nextIsEnd() {
        return isAtEnd() || parserData.tokens().get(parserData.current() + 1).type() == TokenType.EOF;
    }

    public Token peek() {
        return parserData.tokens().get(parserData.current());
    }

    public Token previous() {
        return parserData.tokens().get(parserData.current() - 1);
    }

    public boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    public Token consume(TokenType type) {
        if (match(type)) return previous();
        throw expectedError(type);
    }

    public Parser.ParseError expectedError(Object... types) {
        var msg = "expected";

        if (types.length == 1) {
            msg += " %s";
        } else {
            msg += ", %s".repeat(types.length - 1);
            msg += " or %s";
        }

        var wrapped = Arrays.stream(types)
                .map(obj -> obj instanceof TokenType type
                        ? "'" + type + "'"
                        : obj.toString()
                )
                .toArray();

        parserData.errors().error(peek().line(), msg, wrapped);
        return new Parser.ParseError();
    }

    public Parser.ParseError error(Token token, String message) {
        parserData.errors().error(token.line(), "at token %s:%s".formatted(token.lexeme(), message));
        return new Parser.ParseError();
    }
}
