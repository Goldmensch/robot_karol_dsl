package io.github.goldmensch.compiler.validation.rules;

import io.github.goldmensch.compiler.ast.AstRoot;
import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.ast.Statement;
import io.github.goldmensch.compiler.ast.TopLevelConstruct;
import io.github.goldmensch.compiler.codegeneration.RobotKarolUtils;
import io.github.goldmensch.compiler.lexing.Token;
import io.github.goldmensch.compiler.validation.AstValidator;
import io.github.goldmensch.compiler.validation.SemanticRule;

import java.util.List;

public class CannotCallUnknownMethod implements SemanticRule {
    @Override
    public void validate(AstRoot head, AstValidator validator) {
        List<String> methods = head.topLevelConstructs()
                .stream()
                .filter(construct -> !(construct instanceof TopLevelConstruct.Main))
                .map(validator::methodName)
                .toList();

        head.traverse(null, (node, o) -> {
            if (node instanceof Expression.CondCall condCall) {
                String identifier = condCall.identifier();
                if (!RobotKarolUtils.isPredefinedCondition(condCall) && !methods.contains(identifier)) {
                    validator.error(node, "Unknown condition called %s(%s)", identifier, formatArg(condCall.argument()));
                }
            }

            if (node instanceof Statement.FuncCall funcCall) {
                String identifier = funcCall.identifier();
                if (!RobotKarolUtils.isPredefinedFunction(funcCall) && !methods.contains(identifier)) {
                    validator.error(node, "Unknown function called %s(%s)", identifier, formatArg(funcCall.argument()));
                }
            }
            return false;
        });
    }

    private String formatArg(Token arg) {
        return arg != null
                ? arg.lexeme()
                : "";
    }
}
