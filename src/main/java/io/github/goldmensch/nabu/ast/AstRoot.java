package io.github.goldmensch.nabu.ast;

import java.util.List;
import java.util.function.BiFunction;

public record AstRoot(
        List<TopLevelConstruct> topLevelConstructs
) implements Node {
    @Override
    public <T> void traverse(T context, BiFunction<Node, T, Boolean> function) {
        if (function.apply(this, context)) return;
        for (TopLevelConstruct topLevelConstruct : topLevelConstructs) {
            topLevelConstruct.traverse(context, function);
        }
    }
}
