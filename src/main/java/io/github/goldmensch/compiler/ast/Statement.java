package io.github.goldmensch.compiler.ast;

import io.github.goldmensch.compiler.lexing.Token;

import java.util.List;
import java.util.function.BiFunction;

public sealed interface Statement extends Node permits Statement.Block, Statement.DoWhile, Statement.Fast, Statement.FuncCall, Statement.If, Statement.Loop, Statement.Return, Statement.Slow, Statement.Test, Statement.Times, Statement.Try, Statement.While {

    record Return(Expression value, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            value.traverse(context, function);
        }
    }

    record FuncCall(String identifier, Token argument, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) {
            }
        }
    }

    record Block(List<Statement> statements) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            for (Statement statement : statements) {
                statement.traverse(context, function);
            }
        }
    }

    record If(Expression cond, Block thenBlock, Block elseBlock, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            cond.traverse(context, function);
            thenBlock.traverse(context, function);
        }
    }

    record While(Expression cond, Block block, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            cond.traverse(context, function);
            block.traverse(context, function);
        }
    }

    record DoWhile(Block block, Expression cond, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
            cond.traverse(context, function);
        }
    }

    record Times(int number, Block block, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }

    record Loop(Block block, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }

    record Test(String condCallIdentifier, List<Token> arguments, List<Block> statements,
                Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            for (Block statement : statements) {
                statement.traverse(context, function);
            }
        }
    }

    record Try(List<Expression> conditions, List<Block> statements, Position position) implements Statement {

        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            for (Expression condition : conditions) {
                condition.traverse(context, function);
            }
            for (Block statement : statements) {
                statement.traverse(context, function);
            }
        }
    }


    record Fast(Block block, Position position) implements Statement {
        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }

    record Slow(Block block, Position position) implements Statement {

        @Override
        public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
            if (function.apply(this, context)) return;
            block.traverse(context, function);
        }
    }
}
