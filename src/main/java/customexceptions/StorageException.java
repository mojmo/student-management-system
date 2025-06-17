package customexceptions;

/**
 * Exception thrown for errors during storage operations
 */
public class StorageException extends RuntimeException {
    public StorageException(String message) {
        super(message);
    }
    
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}