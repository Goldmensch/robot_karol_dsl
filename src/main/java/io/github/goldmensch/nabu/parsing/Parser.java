package io.github.goldmensch.nabu.parsing;

import io.github.goldmensch.nabu.ErrorHandler;
import io.github.goldmensch.nabu.ast.AstRoot;
import io.github.goldmensch.nabu.ast.TopLevelConstruct;
import io.github.goldmensch.nabu.lexing.Token;
import io.github.goldmensch.nabu.parsing.subparsers.ExpressionSubParser;
import io.github.goldmensch.nabu.parsing.subparsers.StatementSubParser;
import io.github.goldmensch.nabu.parsing.subparsers.TopLevelSubParser;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final TopLevelSubParser topLevelParser;

    public Parser(List<Token> tokens, ErrorHandler errorHandler) {
        ParserData parserData = new ParserData(tokens, errorHandler);
        var expressionParser = new ExpressionSubParser(parserData);
        var statementParser = new StatementSubParser(parserData);
        this.topLevelParser = new TopLevelSubParser(parserData);

        parserData.init(expressionParser, statementParser, this.topLevelParser);
    }

    public AstRoot parse() {
        var topLevelConstructs = new ArrayList<TopLevelConstruct>();
        try {
            while (!topLevelParser.isAtEnd()) {
                topLevelConstructs.add(topLevelParser.parse());
            }
        } catch (ParseError ignored) {
        }
        return new AstRoot(topLevelConstructs);
    }

    public static class ParseError extends RuntimeException {
    }
}
