package Parser.AST;

import Interfaces.Evaluator;
import Parser.DoneException;

import java.util.Map;

// IfStatement: if (Expression) then Statement else Statement
public class IfStatement extends Statement implements Evaluator {
    private final Expression condition;
    private final Statement thenBranch;
    private final Statement elseBranch;

    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        long conditionValue = condition.evaluate(context);

        try {
            if (conditionValue > 0) {
                return thenBranch.evaluate(context);
            } else if (elseBranch != null) {
                return elseBranch.evaluate(context);
            }
        } catch (DoneException e) {
            System.out.println("Execution stopped due to DoneCommand inside IfStatement.");
            throw e; // Ensure `done` immediately stops execution
        }

        return 0;
    }

    @Override
    public String toString() {
        return "if (" + condition.toString() + ") then " + thenBranch.toString() + " else " + (elseBranch != null ? elseBranch.toString() : "nothing");
    }
}
