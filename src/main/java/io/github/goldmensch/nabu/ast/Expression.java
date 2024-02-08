package io.github.goldmensch.nabu.ast;

import io.github.goldmensch.nabu.lexing.Token;

import java.util.function.BiFunction;

public sealed interface Expression extends Node permits Expression.Binary, Expression.CondCall, Expression.DefaultTryArm, Expression.Grouping, Expression.Literal, Expression.Unary {
    record Binary(Expression left, Token operator, Expression right) implements Expression {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            left.traverse(context, function);
            right.traverse(context, function);
        }
    }

    record Unary(Token operator, Expression expr) implements Expression {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            expr.traverse(context, function);
        }
    }

    record Grouping(Expression expr) implements Expression {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            expr.traverse(context, function);
        }
    }

    record CondCall(String identifier, Token argument, Position position) implements Expression {

        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            function.apply(this, context);
        }
    }

    record Literal(Token literal) implements Expression {
        @Override
        public Position position() {
            return new Position(literal);
        }

        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            function.apply(this, context);
        }
    }

    record DefaultTryArm(int line) implements Expression {

        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            function.apply(this, context);
        }

        @Override
        public Position position() {
            return new Position(line, null);
        }
    }
}
