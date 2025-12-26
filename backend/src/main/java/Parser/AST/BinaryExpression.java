package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

public class BinaryExpression extends Expression implements Evaluator{
    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        long leftValue = left.evaluate(context);
        long rightValue = right.evaluate(context);

        switch (operator) {
            case "+":
                return leftValue + rightValue;
            case "-":
                return leftValue - rightValue;
            case "*":
                return leftValue * rightValue;
            case "/":
                return leftValue / rightValue;
            case "%":
                return leftValue % rightValue;
            case "^":
                return (long) Math.pow(leftValue, rightValue);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
    }
}
