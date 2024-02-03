package io.github.goldmensch.compiler.lexing;

public record Token(
        TokenType type,
        String lexeme,
        Object literal,
        int line
) {
}
