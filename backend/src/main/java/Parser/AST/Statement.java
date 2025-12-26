package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

// Abstract base class for all statements
public abstract class Statement extends ASTNode implements Evaluator {
    public abstract long evaluate(Map<String, Object> context);
}
