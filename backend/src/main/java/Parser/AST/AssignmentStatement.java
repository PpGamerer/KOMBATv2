package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

public class AssignmentStatement extends Command implements Evaluator {
    private final String identifier;
    private final Expression expression;

    public AssignmentStatement(String identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        long value = expression.evaluate(context);
        context.put(identifier, value);
        return value;
    }

    @Override
    public String toString() {
        return "Assign " + identifier + " = " + expression.toString();
    }
}
