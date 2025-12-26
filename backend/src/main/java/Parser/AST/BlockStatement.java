package Parser.AST;

import Interfaces.Evaluator;
import Parser.DoneException;

import java.util.List;
import java.util.Map;

// BlockStatement: { Statement* }
public class BlockStatement extends Statement implements Evaluator {
    private final List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        long result = 0;
        try {
            for (Statement stmt : statements) {
                result = stmt.evaluate(context);
            }
        } catch (DoneException e) {
            System.out.println("Execution stopped due to DoneCommand inside BlockStatement.");
            throw e; // Stop execution immediately
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Statement stmt : statements) {
            sb.append("  ").append(stmt.toString()).append("\n");  // Recursively print the statement
        }
        sb.append("}");
        return sb.toString();
    }
}
