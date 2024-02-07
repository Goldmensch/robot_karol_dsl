package io.github.goldmensch.nabu.ast;

import java.util.function.BiFunction;

public sealed interface Node permits AstRoot, Expression, Statement, TopLevelConstruct {
    default Position position() {
        return null;
    }

    <T> void traverse(T context, BiFunction<Node, T, Boolean> function);
}
