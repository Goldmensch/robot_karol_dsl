package io.github.goldmensch.nabu.lexing;

public record Token(
        TokenType type,
        String lexeme,
        Object literal,
        int line
) {
}
