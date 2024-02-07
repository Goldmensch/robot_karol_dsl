package io.github.goldmensch.nabu.validation.rules;

import io.github.goldmensch.nabu.ast.AstRoot;
import io.github.goldmensch.nabu.ast.Expression;
import io.github.goldmensch.nabu.ast.Statement;
import io.github.goldmensch.nabu.ast.TopLevelConstruct;
import io.github.goldmensch.nabu.codegeneration.RobotKarolUtils;
import io.github.goldmensch.nabu.lexing.Token;
import io.github.goldmensch.nabu.validation.AstValidator;
import io.github.goldmensch.nabu.validation.SemanticRule;

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
            var methodName = validator.methodName(node);
            if (methodName == null) return true;


            if (node instanceof Expression.CondCall condCall) {
                String identifier = condCall.identifier();
                if (!RobotKarolUtils.isPredefinedCondition(condCall) && !isDefinedBefore(condCall.identifier(), methodName, methods)) {
                    validator.error(node, "Unknown condition called %s(%s)", identifier, formatArg(condCall.argument()));
                }
            }

            if (node instanceof Statement.FuncCall funcCall) {
                String identifier = funcCall.identifier();
                if (!RobotKarolUtils.isPredefinedFunction(funcCall) && !isDefinedBefore(funcCall.identifier(), methodName, methods)) {
                    validator.error(node, "Unknown function called %s(%s)", identifier, formatArg(funcCall.argument()));
                }
            }
            return false;
        });
    }

    private boolean isDefinedBefore(String name, String currentMethod, List<String> methodNames) {
        return methodNames.contains(name) && (methodNames.indexOf(name) > methodNames.indexOf(currentMethod));

    }

    private String formatArg(Token arg) {
        return arg != null
                ? arg.lexeme()
                : "";
    }
}
