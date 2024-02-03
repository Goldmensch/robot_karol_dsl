package io.github.goldmensch.compiler.parsing;

import io.github.goldmensch.compiler.ErrorHandler;
import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.parsing.subparsers.ExpressionSubParser;
import io.github.goldmensch.compiler.parsing.subparsers.StatementSubParser;
import io.github.goldmensch.compiler.parsing.subparsers.TopLevelSubParser;

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
