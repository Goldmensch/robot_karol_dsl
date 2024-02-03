package io.github.goldmensch.compiler.validation;

import io.github.goldmensch.compiler.ast.AstRoot;

public interface SemanticRule {
    void validate(AstRoot head, AstValidator validator);
}
