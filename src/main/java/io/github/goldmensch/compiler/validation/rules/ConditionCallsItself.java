package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class ConditionCallsItself implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (node, __) -> {
            if (node instanceof TopLevelConstruct.Condition condition) {
                condition.block().traverse(null, (subNode, ___) -> {
                    if (subNode instanceof Expression.CondCall condCall && condCall.identifier().equals(condition.identifier())) {
                        validator.error(condCall, "Cannot call condition %s recursively.".formatted(condCall.identifier()));
                    }
                    return false;
                });
            }
            return false;
        });
    }
}
