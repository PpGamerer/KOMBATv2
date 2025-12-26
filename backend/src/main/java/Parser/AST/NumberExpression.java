package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

public class NumberExpression extends Expression implements Evaluator {
    private final long value;

    public NumberExpression(long value) {
        this.value = value;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
