package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.codegeneration.RobotKarolUtils;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

public class IllegalCondFuncName implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        head.traverse(null, (node, o) -> {
            String id = switch (node) {
                case TopLevelConstruct.Function func -> func.identifier();
                case TopLevelConstruct.Condition cond -> cond.identifier();
                default -> null;
            };

            if (id != null) {
                if (id.startsWith("cond__")) {
                    validator.error(node, "Conditions and function name %s is not allowed to begin with cond__", id);
                }
                if (RobotKarolUtils.isRobotKarolReserved(id)) {
                    validator.error(node, "Conditions or function name %s is reserved keyword of the robot karol language", id);
                }
            }
            return false;
        });
    }
}
