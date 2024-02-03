package io.github.goldmensch.compiler.validation;

import io.github.goldmensch.compiler.ErrorHandler;
import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Node;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.validation.rules.*;

import java.util.List;

public class AstValidator {

    private final AstRoot ast;
    private final ErrorHandler errors;

    private final List<SemanticRule> rules = List.of(
            new DuplicatedMainMethods(),
            new DuplicatedMethodNames(),
            new ConditionMustReturn(),
            new MainAndFunctionNoReturn(),
            new OnlyOneDefaultArm(),
            new CannotCallUnknownMethod(),
            new ConditionCallsItself(),
            new DefaultArmMustBeLast(),
            new IllegalCondFuncName()
    );

    public AstValidator(AstRoot ast, ErrorHandler errors) {
        this.ast = ast;
        this.errors = errors;
    }

    public void validate() {
        for (SemanticRule rule : rules) {
            rule.validate(ast, this);
        }
    }

    public String methodName(Node construct) {
        return switch (construct) {
            case TopLevelConstruct.Condition cond -> cond.identifier();
            case TopLevelConstruct.Function func -> func.identifier();
            case TopLevelConstruct.Main __ -> "main";
            default -> null;
        };
    }

    public void error(Node node, String message, Object... args) {
        errors.error(node.position().line(), message, args);
    }

    public ErrorHandler errors() {
        return errors;
    }

}
