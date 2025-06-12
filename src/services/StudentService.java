package services;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

import utils.Generators;
import utils.Validator;
import customexceptions.InvalidEmailException;
import customexceptions.InvalidAgeException;
import customexceptions.InvalidGpaException;
import models.Student;
import models.Storage;
import models.FileStorage;

public class StudentService {
    public static void addStudent(Scanner input) {
        System.out.println("--- Add New Student ---\n");
        String id, name = null, email = null, course = null;
        int age = 0;
        double gpa = 5.0;

        while (name == null) {
            System.out.print("Enter your name: ");
            try {
                name = input.nextLine().trim();
                System.out.println();
                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty!");
                    name = null;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        while (email == null) {
            System.out.print("Enter your email: ");
            String tempEmail = input.nextLine().trim();
            System.out.println();
            try {
                Validator.isValidEmail(tempEmail);
                email = tempEmail;
            } catch (InvalidEmailException e) {
                System.out.println(e.getMessage());
            }
        }

        while (age == 0) {
            System.out.print("Enter your age: ");
            try {
                int tempAge = Integer.parseInt(input.nextLine());
                System.out.println();
                Validator.isValidAge(tempAge);
                age = tempAge;
            } catch (NumberFormatException e) {
                System.out.println("Age must be a number!");
            } catch (InvalidAgeException e) {
                System.out.println(e.getMessage());
            }
        }

        while (course == null) {
            System.out.print("Enter your course: ");
            course = input.nextLine().trim();
            System.out.println();
            if (course.isEmpty()) {
                System.out.println("Course cannot be empty!");
                course = null;
            }
        }

        while (gpa == 5.0) {
            System.out.print("Enter your GPA: ");
            try {
                double tempGpa = Double.parseDouble(input.nextLine());
                Validator.isValidGpa(tempGpa);
                gpa = tempGpa;
                System.out.println();
            } catch (NumberFormatException e) {
                System.out.println("GPA must be a number!");
            } catch (InvalidGpaException e) {
                System.out.println(e.getMessage());
            }

        }

        id = "ST" + Generators.generateId();
        Student student = new Student(id, name, email, age, course, gpa);
        Storage<Student> fileStorage = new FileStorage<>();
        Map<String, Object> map = new HashMap<>();
        map.put("model", "Student");
        map.put("obj", student);
        map.put("fileHeader", Student.FILE_HEADER);
        fileStorage.add(map);
        System.out.println("\n***** Student added successfully! *****\n");
    }
}
