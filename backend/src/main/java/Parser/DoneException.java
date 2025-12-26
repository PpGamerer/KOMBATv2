package Parser;

/**
 * DoneException is thrown when the DoneCommand is executed, signaling
 * the immediate termination of the current strategy execution.
 */
public class DoneException extends RuntimeException {
    public DoneException() {
        super("DoneCommand executed. Stopping strategy evaluation.");
    }
}
