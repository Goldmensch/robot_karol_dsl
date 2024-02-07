package io.github.goldmensch.nabu.validation;

import io.github.goldmensch.nabu.ast.AstRoot;

public interface SemanticRule {
    void validate(AstRoot head, AstValidator validator);
}
