package Parser.AST;

import Interfaces.Evaluator;
import Parser.DoneException;

import java.util.Map;

public class DoneCommand extends ActionCommand implements Evaluator {
    @Override
    public long evaluate(Map<String, Object> context) {
        throw new DoneException(); // Stop execution immediately
    }

    @Override
    public String toString() {
        return "done";
    }
}
