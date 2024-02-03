package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class OnlyOneDefaultArm implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (node, o) -> {
            if (node instanceof Statement.Test test) {
                test.arguments().stream()
                        .filter(token -> token.type() == TokenType.UNDERSCORE)
                        .skip(1)
                        .forEach(token -> validator.errors().error(token.line(), "test construct can only contain one default arm"));
            }

            if (node instanceof Statement.Try tryStm) {
                tryStm.conditions().stream()
                        .filter(expression -> expression instanceof Expression.DefaultTryArm)
                        .skip(1)
                        .forEach(expression -> validator.error(expression, "try construct can only contain one default arm"));
            }
            return false;
        });
    }
}
