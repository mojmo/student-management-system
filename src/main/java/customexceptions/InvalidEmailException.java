package customexceptions;

public class InvalidEmailException extends RuntimeException {
    private final String email;
    public InvalidEmailException(String message) {
        super(message);
        this.email = null;
    }

    public InvalidEmailException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        if (email != null) {
            return "InvalidEmailException: " + getMessage() + " [Email: " + email + "]";
        }
        return "InvlidEmailException: " + getMessage();
    }
}
