package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

// Abstract base class for all expressions
public abstract class Expression extends ASTNode implements Evaluator {
    public abstract long evaluate(Map<String, Object> context);
}
