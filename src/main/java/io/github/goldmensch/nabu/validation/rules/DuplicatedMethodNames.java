package io.github.goldmensch.nabu.validation.rules;

import io.github.goldmensch.nabu.ast.AstRoot;
import io.github.goldmensch.nabu.validation.AstValidator;
import io.github.goldmensch.nabu.validation.SemanticRule;

import java.util.HashSet;
import java.util.Set;

public class DuplicatedMethodNames implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(new Context(), (node, context) -> {
            String name = validator.methodName(node);
            if (name != null && !context.knownMethods.add(name)) {
                validator.error(node, "A condition or function with the same name %s already exists", name);
            }
            return false;
        });
    }

    private static class Context {
        final Set<String> knownMethods = new HashSet<>();
    }
}
