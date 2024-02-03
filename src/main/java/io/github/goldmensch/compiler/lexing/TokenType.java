package io.github.goldmensch.compiler.lexing;

import java.util.HashMap;
import java.util.Map;

public enum TokenType {
    // Single character tokens
    LEFT_PAREN("("), RIGHT_PAREN(")"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
    AMPERSAND("&"), PIPE("|"), EXCLAMATION_MARK("!"),

    // Literals
    IDENTIFIER("identifier"), NUMBER("number"), UNDERSCORE("_"),

    // keywords
    FUN("fun"), COND("cond"), MAIN("main"), IF("if"), WHILE("while"), DO("do"), LOOP("loop"), TRY("try"), TEST("test"), TIMES("times"), ElSE("else"),
    YELLOW("yellow"), RED("red"), BLUE("blue"), GREEN("green"),

    RETURN("return"), TRUE("true"), FALSE("false"),

    ARROW("->"),

    FAST("fast"), SLOW("slow"),

    EOF("end of file");

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("fun", FUN);
        KEYWORDS.put("cond", COND);
        KEYWORDS.put("if", IF);
        KEYWORDS.put("else", ElSE);
        KEYWORDS.put("main", MAIN);
        KEYWORDS.put("while", WHILE);
        KEYWORDS.put("do", DO);
        KEYWORDS.put("loop", LOOP);
        KEYWORDS.put("try", TRY);
        KEYWORDS.put("test", TEST);
        KEYWORDS.put("times", TIMES);


        KEYWORDS.put("yellow", YELLOW);
        KEYWORDS.put("red", RED);
        KEYWORDS.put("blue", BLUE);
        KEYWORDS.put("green", GREEN);

        KEYWORDS.put("return", RETURN);
        KEYWORDS.put("true", TRUE);
        KEYWORDS.put("false", FALSE);
        KEYWORDS.put("fast", FAST);
        KEYWORDS.put("slow", SLOW);
    }

    private final String representation;

    TokenType(String representation) {
        this.representation = representation;
    }

    public static TokenType keyword(String identifier) {
        return KEYWORDS.get(identifier);
    }

    public static boolean isKeyword(TokenType type) {
        return KEYWORDS.containsValue(type);
    }

    @Override
    public String toString() {
        return representation;
    }
}
