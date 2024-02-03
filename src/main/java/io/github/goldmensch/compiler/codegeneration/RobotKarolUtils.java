package io.github.goldmensch.compiler.codegeneration;

import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.lexing.TokenType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotKarolUtils {
    public static final List<TokenType> SOME_COLOR = List.of(TokenType.RED, TokenType.BLUE, TokenType.GREEN, TokenType.YELLOW);
    private static final Map<PredefinedKey, String> predefinedFunctions = new HashMap<>();
    private static final Map<PredefinedKey, String> predefinedConditions = new HashMap<>();

    static {
        // predefined functions
        predefinedFunctions.put(new PredefinedKey("step"), "Schritt");
        predefinedFunctions.put(new PredefinedKey("step", List.of(TokenType.NUMBER)), "Schritt");
        predefinedFunctions.put(new PredefinedKey("turnLeft"), "LinksDrehen");
        predefinedFunctions.put(new PredefinedKey("turnRight"), "RechtsDrehen");
        predefinedFunctions.put(new PredefinedKey("putBrick"), "Hinlegen");
        predefinedFunctions.put(new PredefinedKey("putBrick", SOME_COLOR), "Hinlegen");
        predefinedFunctions.put(new PredefinedKey("putBrick", List.of(TokenType.NUMBER)), "Hinlegen");
        predefinedFunctions.put(new PredefinedKey("pickBrick"), "Aufheben");
        predefinedFunctions.put(new PredefinedKey("putBrick", List.of(TokenType.NUMBER)), "Aufheben");
        predefinedFunctions.put(new PredefinedKey("setMark"), "MarkeSetzen");
        predefinedFunctions.put(new PredefinedKey("setMark", SOME_COLOR), "MarkeSetzen");
        predefinedFunctions.put(new PredefinedKey("deleteMark", SOME_COLOR), "MarkeLöschen");
        predefinedFunctions.put(new PredefinedKey("wait"), "Warten");
        predefinedFunctions.put(new PredefinedKey("wait", List.of(TokenType.NUMBER)), "Warten");
        predefinedFunctions.put(new PredefinedKey("beep"), "Ton");
        predefinedFunctions.put(new PredefinedKey("stop"), "Beenden");

        // predefined conditions
        predefinedConditions.put(new PredefinedKey("isWall"), "istWand");
        predefinedConditions.put(new PredefinedKey("isBrick"), "IstZiegel");
        predefinedConditions.put(new PredefinedKey("isBrick", List.of(TokenType.NUMBER)), "IstZiegel");
        predefinedConditions.put(new PredefinedKey("isBrick", SOME_COLOR), "IstZiegel");
        predefinedConditions.put(new PredefinedKey("isMark"), "IstMarke");
        predefinedConditions.put(new PredefinedKey("isMark", SOME_COLOR), "IstMarke");
        predefinedConditions.put(new PredefinedKey("isSouth"), "IstSüden");
        predefinedConditions.put(new PredefinedKey("isNorth"), "IstNorden");
        predefinedConditions.put(new PredefinedKey("isWest"), "IstWesten");
        predefinedConditions.put(new PredefinedKey("isEast"), "IstOsten");
    }

    public static String translateFunction(String id) {
        return translateOrDefault(predefinedFunctions, id);
    }

    public static String translateCondition(String id) {
        return translateOrDefault(predefinedConditions, id);
    }

    public static String translateArgument(Token token) {
        return switch (token.type()) {
            case RED -> "rot";
            case BLUE -> "blau";
            case GREEN -> "grün";
            case YELLOW -> "gelb";
            case NUMBER -> token.literal().toString();
            default -> throw new IllegalArgumentException("Not a valid parameter: %s".formatted(token));
        };
    }

    public static boolean isPredefinedFunction(Statement.FuncCall funcCall) {
        return isPredefined(predefinedFunctions, funcCall.identifier(), funcCall.argument());
    }

    public static boolean isPredefinedCondition(Expression.CondCall condCall) {
        return isPredefined(predefinedConditions, condCall.identifier(), condCall.argument());
    }

    private static String translateOrDefault(Map<PredefinedKey, String> list, String identifier) {
        return list.entrySet()
                .stream()
                .filter(entry -> entry.getKey().name().equals(identifier))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(identifier);
    }

    private static boolean isPredefined(Map<PredefinedKey, String> list, String name, Token arg) {
        return list.keySet()
                .stream()
                .anyMatch(key -> key.name.equals(name) && (arg == null || key.possibleArgument().contains(arg.type())));
    }

    private record PredefinedKey(
            String name,
            List<TokenType> possibleArgument
    ) {
        PredefinedKey(String name) {
            this(name, List.of());
        }
    }
}
