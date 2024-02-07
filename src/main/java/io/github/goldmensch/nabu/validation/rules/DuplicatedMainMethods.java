package io.github.goldmensch.nabu.validation.rules;

import io.github.goldmensch.nabu.ast.AstRoot;
import io.github.goldmensch.nabu.ast.TopLevelConstruct;
import io.github.goldmensch.nabu.validation.AstValidator;
import io.github.goldmensch.nabu.validation.SemanticRule;

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
