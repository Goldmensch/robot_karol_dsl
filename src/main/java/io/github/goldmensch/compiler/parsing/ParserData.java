package io.github.goldmensch.compiler.parsing;

import io.github.goldmensch.compiler.ErrorHandler;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.parsing.subparsers.ExpressionSubParser;
import io.github.goldmensch.compiler.parsing.subparsers.StatementSubParser;
import io.github.goldmensch.compiler.parsing.subparsers.TopLevelSubParser;

import java.util.Collections;
import java.util.List;

public class ParserData {
    private final List<Token> tokens;
    private final ErrorHandler errors;
    private SubParsers subParsers;
    private int current = 0;

    public ParserData(List<Token> tokens, ErrorHandler errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

    public void init(ExpressionSubParser expressionParser, StatementSubParser statementParser, TopLevelSubParser topLevelParser) {
        this.subParsers = new SubParsers(expressionParser, statementParser, topLevelParser);
    }

    public ErrorHandler errors() {
        return errors;
    }

    public List<Token> tokens() {
        return Collections.unmodifiableList(tokens);
    }

    public SubParsers subParsers() {
        return subParsers;
    }

    public int current() {
        return current;
    }

    public void increaseCurrent() {
        current++;
    }

    public record SubParsers(
            ExpressionSubParser expressionParser,
            StatementSubParser statementParser,
            TopLevelSubParser topLevelParser
    ) {
    }
}
