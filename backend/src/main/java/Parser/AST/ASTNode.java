package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

// Base class for all AST nodes
public abstract class ASTNode implements Evaluator {
    public abstract long evaluate(Map<String, Object> context);
}
