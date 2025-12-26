package Parser.AST;

import Interfaces.Evaluator;
import Parser.DoneException;

import java.util.Map;

// WhileStatement: while (Expression) Statement
public class WhileStatement extends Statement implements Evaluator {
    private final Expression condition;
    private final Statement body;

    public WhileStatement(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        int counter = 0;
        try {
            while (condition.evaluate(context) > 0 && counter < 10000) {
                body.evaluate(context);
                counter++;
            }
        } catch (DoneException e) {
            System.out.println("Execution stopped due to DoneCommand inside WhileStatement.");
            throw e; // Stop execution immediately
        }

        if (counter >= 10000) {
            throw new RuntimeException("a while loop that has run for 10000 iterations is terminated");
        }
        return 0;
    }

    @Override
    public String toString() {
        return "while (" + condition.toString() + ") " + body.toString();
    }
}
