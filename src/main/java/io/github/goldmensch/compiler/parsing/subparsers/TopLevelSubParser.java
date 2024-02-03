package io.github.goldmensch.compiler.parsing.subparsers;

import io.github.goldmensch.compiler.ast.Position;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.parsing.ParserData;
import io.github.goldmensch.compiler.parsing.SubParser;

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
        return new TopLevelConstruct.Main(parser().statementParser().block(), new Position(token));
    }

    private TopLevelConstruct fun() {
        Token token = consume(TokenType.FUN);
        consume(TokenType.IDENTIFIER);
        return new TopLevelConstruct.Function(((String) previous().literal()), parser().statementParser().block(), new Position(token));
    }

    private TopLevelConstruct cond() {
        Token token = consume(TokenType.COND);
        consume(TokenType.IDENTIFIER);
        return new TopLevelConstruct.Condition(((String) previous().literal()), parser().statementParser().block(), new Position(token));
    }
}
