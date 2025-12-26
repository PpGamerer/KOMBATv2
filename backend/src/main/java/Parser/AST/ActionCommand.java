package Parser.AST;


import Interfaces.Evaluator;

import java.util.Map;

// Abstract base class for action commands
abstract class ActionCommand extends Command implements Evaluator {
    public abstract long evaluate(Map<String, Object> context);
}