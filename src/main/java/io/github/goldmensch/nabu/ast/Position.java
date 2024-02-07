package io.github.goldmensch.nabu.ast;

import io.github.goldmensch.nabu.lexing.Token;

public record Position(
        int line,
        String lexeme
) {
    public Position(Token token) {
        this(token.line(), token.lexeme());
    }
}
