package Parser.AST;

import Interfaces.Evaluator;

import java.util.Map;

public class IdentifierExpression extends Expression implements Evaluator {
    private final String name;

    public IdentifierExpression(String name) {
        this.name = name;
    }

    @Override
    public long evaluate(Map<String, Object> context) {
        Object value = context.getOrDefault(name, 0L);
        switch (name) {
            case "row":
                return (long) context.getOrDefault("row", 0L);  // Get the current row
            case "col":
                return (long) context.getOrDefault("col", 0L);  // Get the current column
            case "budget":
                return (long) (value instanceof Double ? (Double) value : (long) value);  // Get the player’s remaining budget
            case "int":
                return (long) context.getOrDefault("interest", 0L);  // Get the interest percentage
            case "maxbudget":
                return (long) context.getOrDefault("maxbudget", 0L);  // Get the maximum budget
            case "spawnsleft":
                return (long) context.getOrDefault("spawnsleft", 0L);  // Get remaining spawns
            case "random":
                return (long) (Math.random() * 1000);  // Generate random value between 0 and 999
            default:
                // Fallback for regular variables
                return (long) context.getOrDefault(name, 0L);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
