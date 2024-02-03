package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class DefaultArmMustBeLast implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (node, o) -> {
            if (node instanceof Statement.Test test) {
                boolean hasDefault = test.arguments().stream()
                        .anyMatch(token -> token.type() == TokenType.UNDERSCORE);
                var lastArm = test.arguments().getLast();
                if (hasDefault && lastArm.type() != TokenType.UNDERSCORE) {
                    validator.errors().error(lastArm.line(), "Default arm must be last in test statement");
                }
            }

            if (node instanceof Statement.Try tryStm) {
                boolean hasDefault = tryStm.conditions().stream()
                        .anyMatch(expression -> expression instanceof Expression.DefaultTryArm);
                var lastArm = tryStm.conditions().getLast();
                if (hasDefault && !(lastArm instanceof Expression.DefaultTryArm)) {
                    validator.error(lastArm, "Default arm must be last in try statement");
                }
            }
            return false;
        });
    }
}
