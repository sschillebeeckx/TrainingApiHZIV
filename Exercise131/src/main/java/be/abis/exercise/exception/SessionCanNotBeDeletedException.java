package be.abis.exercise.exception;

public class SessionCanNotBeDeletedException extends RuntimeException {
    public SessionCanNotBeDeletedException(String message) {
        super(message);
    }
}
