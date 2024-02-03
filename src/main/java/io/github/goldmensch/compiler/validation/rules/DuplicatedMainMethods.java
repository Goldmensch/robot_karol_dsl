package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class DuplicatedMainMethods implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(new Context(), (node, context) -> {
            if (node instanceof TopLevelConstruct.Main && context.setMain) {
                validator.error(node, "The program can only contain one main function.");
            }
            context.setMain = context.setMain | node instanceof TopLevelConstruct.Main;
            return false;
        });
    }

    private static class Context {
        boolean setMain = false;
    }
}
