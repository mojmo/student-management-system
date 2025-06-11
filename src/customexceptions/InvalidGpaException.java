package customexceptions;

public class InvalidGpaException extends RuntimeException {
    public InvalidGpaException(String message) {
        super(message);
    }
}
