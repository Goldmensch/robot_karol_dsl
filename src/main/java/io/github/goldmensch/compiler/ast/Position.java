package io.github.goldmensch.compiler.ast;

import io.github.goldmensch.compiler.lexing.Token;

public record Position(
        int line,
        String lexeme
) {
    public Position(Token token) {
        this(token.line(), token.lexeme());
    }
}
