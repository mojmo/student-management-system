package utils;

import customexceptions.InvalidAgeException;
import customexceptions.InvalidEmailException;
import customexceptions.InvalidGpaException;

public class Validator {

    public static void isValidEmail(String email) throws InvalidEmailException {
        if (!email.contains("@")) {
            throw new InvalidEmailException("Invalid email: " + email + ". Email must contain @ symbol.");
        } else if (!email.contains(".")) {
            throw new InvalidEmailException("Invalid email: " + email + ". Email must contain . symbol.");
        }
    }

    public static void isValidAge(int age) throws InvalidAgeException {
        if (age < 16 || age > 99) {
            throw new InvalidAgeException("Age must be between 16 and 99.");
        }
    }

    public static void isValidGpa(double gpa) throws InvalidGpaException {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new InvalidGpaException("GPA must be between 0.0 and 4.0");
        }
    }
}
