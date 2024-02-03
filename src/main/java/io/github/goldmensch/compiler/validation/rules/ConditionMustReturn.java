package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class ConditionMustReturn implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (method, o) -> {
            if (method instanceof TopLevelConstruct.Condition cond) {
                Context context = new Context();
                method.traverse(context, (node, __) -> {
                    context.hasReturnValue = context.hasReturnValue | node instanceof Statement.Return;
                    return false;
                });
                if (!context.hasReturnValue) {
                    validator.error(method, "Conditions %s must contain at minimum one return statement!", cond.identifier());
                }
            }
            return false;
        });
    }

    private static class Context {
        boolean hasReturnValue = false;
    }
}
