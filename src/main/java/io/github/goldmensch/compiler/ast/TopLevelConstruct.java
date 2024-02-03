package io.github.goldmensch.compiler.ast;

import java.util.function.BiFunction;

public sealed interface TopLevelConstruct extends Node {
    record Main(Statement.Block block, Position position) implements TopLevelConstruct {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }

    record Function(String identifier, Statement.Block block, Position position) implements TopLevelConstruct {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }

    record Condition(String identifier, Statement.Block block, Position position) implements TopLevelConstruct {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }
}
