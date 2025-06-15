package utils;

import customexceptions.InvalidAgeException;
import customexceptions.InvalidEmailException;
import customexceptions.InvalidGpaException;

import java.util.regex.Pattern;

public class Validator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 99;

    private static final double MIN_GPA = 0.0;
    private static final double MAX_GPA = 4.0;

    /**
     * Validate email format using regex pattern
     * @param email Email address to validate
     * @throws InvalidEmailException If email format is invalid
     */
    public static void isValidEmail(String email) throws InvalidEmailException {

        if (email == null || email.trim().isEmpty())
            throw new InvalidEmailException("Email cannot be empty");
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + email +
                    ". Please use a valid email format (e.g., user@example.com)");
        }
    }

    /**
     * Validates that age is within acceptable range
     * @param age Age to validate
     * @throws InvalidAgeException If age is outside acceptable range
     */
    public static void isValidAge(int age) throws InvalidAgeException {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new InvalidAgeException(
                String.format("Age must be between %d and %d. You entered: %d",
                MIN_AGE, MAX_AGE, age)
            );
        }
    }

    /**
     * Validate that GPA is within the acceptable range
     * @param gpa GPA to validate
     * @throws InvalidGpaException if GPA is outside acceptable range
     */
    public static void isValidGpa(double gpa) throws InvalidGpaException {
        if (gpa < MIN_GPA || gpa > MAX_GPA) {
            throw new InvalidGpaException(
                String.format("GPA must be between %.1f and %.1f. You entered: %.2f",
                MIN_GPA, MAX_GPA, gpa)
            );
        }
    }

    /**
     * Validates that a string is not empty
     * 
     * @param value String to validate
     * @param fieldName Name of the field (for the error message)
     * @return true if valid, false otherwise
     */
    public static boolean isNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println(fieldName + " cannot be empty.");
            return false;
        }
        return true;
    }

    /**
     * Validates course name format
     * 
     * @param course Course name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            System.out.println("Course name cannot be empty.");
            return false;
        }
        return true;
    }
}
