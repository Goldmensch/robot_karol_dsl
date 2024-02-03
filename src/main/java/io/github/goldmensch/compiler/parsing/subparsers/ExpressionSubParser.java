package io.github.goldmensch.compiler.parsing.subparsers;

import io.github.goldmensch.compiler.ast.Expression;
import io.github.goldmensch.compiler.lexing.TokenType;
import io.github.goldmensch.compiler.parsing.ParserData;
import io.github.goldmensch.compiler.parsing.SubParser;

public class ExpressionSubParser extends SubParser<Expression> {
    public ExpressionSubParser(ParserData parserData) {
        super(parserData);
    }

    @Override
    public Expression parse() {
        Expression expr = unary();

        while (match(TokenType.PIPE, TokenType.AMPERSAND)) {
            expr = new Expression.Binary(expr, previous(), expression());
        }

        return expr;
    }

    private Expression unary() {
        if (match(TokenType.EXCLAMATION_MARK)) {
            return new Expression.Unary(previous(), unary());
        }
        return primary();
    }

    private Expression primary() {
        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN);
            return new Expression.Grouping(expression);
        }
        if (match(TokenType.TRUE, TokenType.FALSE)) {
            return new Expression.Literal(previous());
        }
        return condCall();
    }

    private Expression.CondCall condCall() {
        if (check(TokenType.IDENTIFIER)) {
            var func = parser().statementParser().funcCall();
            return new Expression.CondCall(func.identifier(), func.argument(), func.position());
        }
        throw expectedError("expression");
    }
}
