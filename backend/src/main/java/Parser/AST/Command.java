package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

public abstract class Command extends Statement implements Evaluator {
    public abstract long evaluate(Map<String, Object> context);
}
