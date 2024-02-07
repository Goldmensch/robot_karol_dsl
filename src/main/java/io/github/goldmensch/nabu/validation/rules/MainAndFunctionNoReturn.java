package io.github.goldmensch.nabu.validation.rules;

import io.github.goldmensch.nabu.ast.AstRoot;
import io.github.goldmensch.nabu.ast.Statement;
import io.github.goldmensch.nabu.ast.TopLevelConstruct;
import io.github.goldmensch.nabu.validation.AstValidator;
import io.github.goldmensch.nabu.validation.SemanticRule;

public class MainAndFunctionNoReturn implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (method, o) -> {
            if (method instanceof TopLevelConstruct.Main || method instanceof TopLevelConstruct.Function) {
                method.traverse(null, (node, __) -> {
                    if (node instanceof Statement.Return) {
                        validator.error(node, "Main or function %s must not contain at minimum one return statement!",
                                validator.methodName(method));
                    }
                    return false;
                });
            }
            return false;
        });
    }
}
