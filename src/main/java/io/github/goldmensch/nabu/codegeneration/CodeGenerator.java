package io.github.goldmensch.nabu.codegeneration;

import io.github.goldmensch.nabu.ast.*;
import io.github.goldmensch.nabu.lexing.Token;
import io.github.goldmensch.nabu.lexing.TokenType;

import java.util.*;

public class CodeGenerator {
    private final AstRoot ast;

    private Writer writer = new Writer();
    private boolean fast = false;

    public CodeGenerator(AstRoot ast) {
        this.ast = ast;
    }

    public String generate() {
        prepare();
        generate(ast);
        return writer.code();
    }

    private void prepare() {
        writer.addExtraOnTop("bedingung cond__true wahr endeBedingung");
        writer.addExtraOnTop("bedingung cond__false falsch endeBedingung");
    }

    private void generate(Node node) {
        switch (node) {
            case AstRoot root -> root.topLevelConstructs().forEach(this::generate);
            case TopLevelConstruct.Main(Statement.Block block, var __) -> writeMethod("programm", "", block);
            case TopLevelConstruct.Function(String id, Statement.Block block, var __) ->
                    writeMethod("anweisung", id, block);
            case TopLevelConstruct.Condition(String id, Statement.Block block, var __) ->
                    writeMethod("bedingung", id, block);

            case Statement.Block block -> writeBlock(block);
            case Statement.FuncCall(String identifier, Token arg, var __) ->
                    writeCall(RobotKarolUtils.translateFunction(identifier), arg);
            case Statement.Return(Expression expr, var __) -> writeReturn(expr);

            case Statement.If(Expression cond, Statement.Block thenBlock, Statement.Block elseBlock, var __) ->
                    writeIf(cond, thenBlock, elseBlock);
            case Statement.Loop(Statement.Block block, var __) -> writeLoop(block);
            case Statement.While(Expression cond, Statement.Block block, var __) -> writeWhile(cond, block);
            case Statement.DoWhile(Statement.Block block, Expression cond, var __) -> writeDoWhile(cond, block);
            case Statement.Times(int number, Statement.Block block, var __) -> writeTimes(number, block);
            case Statement.Test(String id, List<Token> arguments, List<Statement.Block> statements, var __) ->
                    writeTest(id, arguments, statements);
            case Statement.Try(List<Expression> conditions, List<Statement.Block> statements, var __) ->
                    writeTry(conditions, statements);
            case Expression __ ->
                    throw new IllegalArgumentException("expression not supported in root node generation");
            case Statement.Fast(Statement.Block block, var __) -> writeFast(block);
            case Statement.Slow(Statement.Block block, var __) -> writeSlow(block);
        }
    }

    private void writeSlow(Statement.Block block) {
        writer.append("langsam", Writer.BREAK);
        writer.appendSection(() -> generate(block));
        if (fast) {
            writer.append("schnell");
        }
    }

    private void writeBlock(Statement.Block block) {
        for (Statement statement : block.statements()) {
            generate(statement);
            writer.append(Writer.BREAK);
        }
    }

    private void writeFast(Statement.Block block) {
        if (!fast) {
            writer.append("schnell", Writer.BREAK);
            fast = true;

            writer.appendSection(() -> generate(block));

            writer.append("langsam");
            fast = false;
        } else {
            generate(block);
        }
    }

    private void writeTest(String id, List<Token> arguments, List<Statement.Block> statements) {
        if (arguments.isEmpty()) return;

        // generate default arm // Expression.DefaultTryArm simply evaluated to "wenn cond__true dann", so it's basically the last else without a condition
        if (arguments.getFirst().type() == TokenType.UNDERSCORE) {
            expression(new Expression.DefaultTryArm(0), () -> generate(statements.getFirst()), () -> {
            }, Set.of(ExprTag.NEW_IF));
            return;
        }

        expression(new Expression.CondCall(id, arguments.getFirst(), null), () -> generate(statements.getFirst()), () -> {
            List<Token> newArguments = new ArrayList<>(arguments);
            newArguments.removeFirst();
            List<Statement.Block> newStatements = new ArrayList<>(statements);
            newStatements.removeFirst();

            writeTest(id, newArguments, newStatements);
        }, Set.of(ExprTag.NEW_IF));
    }

    private void writeTry(List<Expression> conditions, List<Statement.Block> statements) {
        if (conditions.isEmpty()) return;
        expression(conditions.getFirst(), () -> generate(statements.getFirst()), () -> {
            List<Expression> newConditions = new ArrayList<>(conditions);
            newConditions.removeFirst();
            List<Statement.Block> newStatements = new ArrayList<>(statements);
            newStatements.removeFirst();
            writeTry(newConditions, newStatements);
        }, Set.of(ExprTag.NEW_IF));
    }

    private void writeWhile(Expression cond, Statement.Block block) {
        writer.append("wiederhole", "solange", writeExpressionExtern(cond), Writer.BREAK);
        writer.appendSection(() -> generate(block));
        writer.append("endeWiederhole");
    }

    private void writeDoWhile(Expression cond, Statement.Block block) {
        writer.append("wiederhole", Writer.BREAK);
        writer.appendSection(() -> generate(block));
        writer.append("endeWiederhole", "solange", writeExpressionExtern(cond));
    }

    private void writeTimes(int number, Statement.Block block) {
        writer.append("wiederhole", String.valueOf(number), "mal", Writer.BREAK);
        writer.appendSection(() -> generate(block));
        writer.append("endeWiederhole");
    }

    private String writeExpressionExtern(Expression expression) {
        String condId = "cond__" + UUID.randomUUID().toString().replace("-", "");
        var oldWriter = writer;
        writer = new Writer();
        writer.append("bedingung", condId, Writer.BREAK);
        writer.appendSection(() -> writeReturn(expression));
        writer.append("endeBedingung");
        oldWriter.addExtraOnTop(writer.code());
        writer = oldWriter;
        return condId;
    }

    private void writeLoop(Statement.Block block) {
        writer.append("wiederhole", "immer");
        generate(block);
        writer.append("endeWiederhole");
    }

    private void writeReturn(Expression expression) {
        expression(expression, () -> writer.append("wahr", Writer.BREAK), () -> writer.append("falsch", Writer.BREAK), Set.of(ExprTag.NEW_IF));
    }

    private void expression(Expression expression, final Runnable thenRn, final Runnable elseRn, Set<ExprTag> tags) {
        if (shouldTagGen(expression)) {
            if (tags.contains(ExprTag.NEW_IF)) {
                writer.append("wenn");
            }
            if (tags.contains(ExprTag.NEGATE)) {
                writer.append("nicht");
            }
        }

        switch (expression) {
            case Expression.CondCall(String identifier, Token argument, var __) ->
                    writer.append(genCall(RobotKarolUtils.translateCondition(identifier), argument));
            case Expression.Unary(Token operator, Expression expr) when operator.type() == TokenType.EXCLAMATION_MARK ->
                    expression(expr, thenRn, elseRn, modifierWith(tags, ExprTag.NEGATE));
            case Expression.Grouping(Expression expr) -> expression(expr, thenRn, elseRn, tags);
            case Expression.Literal(Token literal) -> {
                switch (literal.type()) {
                    case TRUE -> writer.append("cond__true");
                    case FALSE -> writer.append("cond__false");
                    default -> throw new IllegalArgumentException("unknown literal: %s".formatted(literal));
                }
            }
            case Expression.Binary(Expression left, Token operator, Expression right) -> {
                TokenType operatorType = operator.type();
                if (tags.contains(ExprTag.NEGATE)) {
                    operatorType = switch (operatorType) {
                        case AMPERSAND -> TokenType.PIPE;
                        case PIPE -> TokenType.AMPERSAND;
                        default ->
                                throw new IllegalArgumentException("unknown binary operator: %s".formatted(operator));
                    };
                }

                Set<ExprTag> passedTags = modifierWith(tags, ExprTag.NEW_IF);
                Runnable modifiedInner = () -> expression(right, thenRn, elseRn, passedTags);
                switch (operatorType) {
                    case AMPERSAND -> expression(left, modifiedInner, elseRn, passedTags);
                    case PIPE -> expression(left, thenRn, modifiedInner, passedTags);
                    default -> throw new IllegalArgumentException("unknown binary operator: %s".formatted(operator));
                }
            }
            case Expression.DefaultTryArm __ -> writer.append("cond__true");
            default -> throw new UnsupportedOperationException("not implemented: %s".formatted(expression));
        }
        if (shouldTagGen(expression)) {
            if (tags.contains(ExprTag.NEW_IF)) {
                writer.append("dann", Writer.BREAK);
                writer.appendSection(thenRn);
                writer.append("sonst", Writer.BREAK);
                writer.appendSection(elseRn);
                writer.append("endeWenn", Writer.BREAK);
            }
        }
    }

    private boolean shouldTagGen(Expression expression) {
        return !(expression instanceof Expression.Grouping || expression instanceof Expression.Binary || expression instanceof Expression.Unary);
    }

    private Set<ExprTag> modifierWith(Set<ExprTag> set, ExprTag e) {
        var local = new HashSet<>(set);

        // negation eliminated itself (neg + neg = pos)
        if (e == ExprTag.NEGATE && local.contains(ExprTag.NEGATE)) {
            local.remove(ExprTag.NEGATE);
            return local;
        }

        local.add(e);

        return local;
    }

    private String genCall(String id, Token arg) {
        String p = arg != null
                ? RobotKarolUtils.translateArgument(arg)
                : "";
        return "%s(%s)".formatted(id, p);
    }

    private void writeIf(Expression expression, Statement.Block block, Statement.Block elseBlock) {
        expression(expression, () -> generate(block), () -> generate(elseBlock), Set.of(ExprTag.NEW_IF));
    }

    private void writeMethod(String type, String identifier, Statement.Block block) {
        Writer oldWriter = writer;
        writer = new Writer();

        writer.append(type, identifier, Writer.BREAK);
        writer.addDepth();
        generate(block);
        writer.subDepth();
        writer.append("ende" + type);

        oldWriter.append(writer.code(), Writer.BREAK);
        writer = oldWriter;
    }

    private void writeCall(String id, Token param) {
        writer.append(genCall(id, param));
    }

    private enum ExprTag {
        NEW_IF,
        NEGATE
    }
}
