package io.github.goldmensch.nabu.lexing;

import io.github.goldmensch.nabu.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private final ErrorHandler errors;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source, ErrorHandler errors) {
        this.source = source;
        this.errors = errors;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add((new Token(TokenType.EOF, "", null, line)));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        // skip meaningless characters
        switch (c) {
            case '\n':
                line++;
            case ' ', '\r', '\t':
                return;
        }

        // match other
        TokenType singleCharType = switch (c) {
            case '(' -> TokenType.LEFT_PAREN;
            case ')' -> TokenType.RIGHT_PAREN;
            case '{' -> TokenType.LEFT_BRACE;
            case '}' -> TokenType.RIGHT_BRACE;
            case '/' -> comment(c);
            case '&' -> TokenType.AMPERSAND;
            case '|' -> TokenType.PIPE;
            case '!' -> TokenType.EXCLAMATION_MARK;
            default -> {
                if (isDigit(c)) {
                    yield number();
                }

                if (c == '_' && peek() == ' ') {
                    yield TokenType.UNDERSCORE;
                }

                if (c == '-' && peek() == '>') {
                    current++;
                    yield TokenType.ARROW;
                }
                if (isAlpha(c)) {
                    yield identifier();
                }
                unexpectedChar(line, c);
                yield null;
            }
        };
        if (singleCharType != null) singleChar(singleCharType);
    }

    private TokenType identifier() {
        while (isAlphaNumeric(peek())) advance();

        var text = source.substring(start, current);
        TokenType keywordType = TokenType.keyword(text);
        addToken(keywordType != null ? keywordType : TokenType.IDENTIFIER, text);
        return null;
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private TokenType number() {
        while (Character.isDigit(peek())) advance();
        addToken(TokenType.NUMBER, Integer.parseInt(source.substring(start, current)));
        return null;
    }

    private TokenType comment(char c) {
        if (c == '/') {
            while (peek() != '\n' && !isAtEnd()) advance();
        } else {
            unexpectedChar(line, c);
        }
        return null;
    }

    private void singleChar(TokenType type) {
        addToken(type, null);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private void addToken(TokenType type, Object literal) {
        var text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    public void unexpectedChar(int line, char c) {
        errors.error(line, "Unexpected character '%s'", c);
    }
}
