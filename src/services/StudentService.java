package services;

import java.util.*;

import utils.Generators;
import utils.Validator;
import customexceptions.InvalidEmailException;
import customexceptions.InvalidAgeException;
import customexceptions.InvalidGpaException;
import models.Student;
import models.Storage;
import models.FileStorage;

public class StudentService {

    static Storage<Student> storage = new FileStorage<>();

    public static List<Student> deserialize(List<String> lines) {
        List<Student> students = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("ID")) continue;
            String[] fields = line.split(",");
            Student student = new Student(
                    fields[0], // id
                    fields[1], // name
                    fields[2], // email
                    Integer.parseInt(fields[3]), // age
                    fields[4], // course
                    Double.parseDouble(fields[5]) // gpa
            );
            students.add(student);
        }
        return students;
    }

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
                System.out.println("Error: " + e.getMessage() + "\n");
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
                System.out.println(e.getMessage() + "\n");
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
                System.out.println(e.getMessage() + "\n");
            }
        }

        while (course == null) {
            System.out.print("Enter your course: ");
            course = input.nextLine().trim();
            System.out.println();
            if (course.isEmpty()) {
                System.out.println("Course cannot be empty!\n");
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
                System.out.println("GPA must be a number!\n");
            } catch (InvalidGpaException e) {
                System.out.println(e.getMessage() + "\n");
            }

        }

        id = "ST" + Generators.generateId();
        Student student = new Student(id, name, email, age, course, gpa);
        Map<String, Object> map = new HashMap<>();
        map.put("model", "Student");
        map.put("obj", student);
        map.put("fileHeader", Student.FILE_HEADER);
        storage.add(map);
        System.out.println("\n***** Student added successfully! *****\n");
    }

    public static void getStudent(Scanner input) {
        System.out.println("--- Search for Student ---\n");
        System.out.print("Enter student ID: ");
        String id = input.nextLine();
        System.out.println();
        String line = storage.get("Student", id);
        String[] student = line.split(",");
        if (line.isEmpty()) {
            System.out.println("Student is not exist :(\n");
        } else {
            System.out.printf(
                    "%-20s | %-30s | %-30s | %-5s | %-25s | %-5s%n",
                    "ID", "NAME", "EMAIL", "AGE", "COURSE", "GPA"
            );
            System.out.printf(
                    "%-20s | %-30s | %-30s | %-5s | %-25s | %-5s\n",
                    student[0].trim(),         // id
                    student[1].trim(),         // name
                    student[2].trim(),         // email
                    Integer.parseInt(student[3].trim()),  // age
                    student[4].trim(),         // course
                    Double.parseDouble(student[5].trim()) // gpa
            );
        }
    }

    public static void removeStudent(Scanner input) {
        System.out.println("--- Remove Student ---\n");
        System.out.print("Enter student ID: ");
        String id = input.nextLine();
        System.out.println();
        String line = storage.get("Student", id);

        if (line.isEmpty()) {
            System.out.println("Student is not exist :(\n");
        } else {
            System.out.print("Are you sure you want to remove this student [Y/N]: ");
            try {
                String choice = input.nextLine().trim().toUpperCase();
                if (choice.equals("Y")) {
                    storage.remove("Student", id);
                    System.out.println("\n***** Student Removed successfully! *****\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    public static void updateStudent(Scanner input) {
        System.out.println("--- Update Student ---\n");
        System.out.print("Enter student ID: ");
        String id = input.nextLine();
        System.out.println();
        String line = storage.get("Student", id);

        if (line.isEmpty()) {
            System.out.println("Student is not exist :(\n");
        } else {
            String[] student = line.split(",");
            String oldName = student[1].trim(), oldEmail = student[2].trim(), oldCourse = student[4].trim();
            int oldAge = Integer.parseInt(student[3].trim());
            double oldGpa = Double.parseDouble(student[5].trim());

            String newName = null, newEmail = null, newCourse = null;
            int newAge = 0;
            double newGpa = 5.0;

            System.out.print("Press Enter to skip the field\n");

            while (newName == null) {
                System.out.print("Enter new name: ");
                try {
                    newName = input.nextLine().trim();
                    System.out.println();
                    if (newName.isEmpty()) {
                        newName = oldName;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + "\n");
                }
            }

            while (newEmail == null) {
                System.out.print("Enter new email: ");
                try {
                    String tempEmail = input.nextLine().trim();
                    System.out.println();
                    if (tempEmail.isEmpty()) {
                        newEmail = oldEmail;
                        break;
                    }
                    Validator.isValidEmail(tempEmail);
                    newEmail = tempEmail;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + "\n");
                }
            }

            while (newAge == 0) {
                System.out.print("Enter your age: ");
                try {
                    String tempInput = input.nextLine();
                    System.out.println();
                    if (tempInput.isEmpty()) {
                        newAge = oldAge;
                        break;
                    }
                    int tempAge = Integer.parseInt(tempInput);
                    Validator.isValidAge(tempAge);
                    newAge = tempAge;
                } catch (InvalidAgeException e) {
                    System.out.println(e.getMessage() + "\n");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + "\n");
                }
            }

            while (newCourse == null) {
                System.out.print("Enter your course: ");
                try {
                    newCourse = input.nextLine().trim();
                    System.out.println();
                    if (!newCourse.isEmpty()) {
                        newCourse = oldCourse;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + "\n");
                }
            }

            while (newGpa == 5.0) {
                System.out.print("Enter your GPA: ");
                try {
                    String tempInput = input.nextLine();
                    if (tempInput.isEmpty()) {
                        newGpa = oldGpa;
                        break;
                    }
                    double tempGpa = Double.parseDouble(tempInput);
                    System.out.println();
                    Validator.isValidGpa(tempGpa);
                    newGpa = tempGpa;
                } catch (InvalidGpaException e) {
                    System.out.println(e.getMessage() + "\n");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage() + "\n");
                }
            }

            try {
                Student newStudent = new Student(id, newName, newEmail, newAge, newCourse, newGpa);
                storage.update("Student", id, newStudent);
                System.out.println("\n***** Student Updated successfully! *****\n");
                System.out.println(newStudent);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }

        }
    }

    public static void getAllStudents() {
        System.out.println("--- Students List ---\n");
        List<String> students = storage.getAll("Student");
        if (students.isEmpty()) {
            System.out.println("There are No Students :(\n");
        } else {
            for (String line : students) {
                String[] student = line.split(",");
                System.out.printf(
                        "%-20s | %-30s | %-30s | %-5s | %-25s | %-5s\n",
                        student[0].trim(),      // id
                        student[1].trim(),      // name
                        student[2].trim(),      // email
                        student[3].trim(),      // age
                        student[4].trim(),      // course
                        student[5].trim()       // gpa
                );
            }
        }
    }

    public static void showStatistics() {
        System.out.println("--- Statistics ---\n");
        List<String> studentsList = storage.getAll("Student");
        List<Student> students = StudentService.deserialize(studentsList);

        if (students.isEmpty()) {
            System.out.println("No students available for statistics. :(");
            return;
        }

        System.out.println("Number of Students: " + students.size());

        double averageGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
        System.out.printf("Average GPA: %.2f\n", averageGpa);

        Student highestGpaStudent = students.stream()
                .max(Comparator.comparingDouble(Student::getGpa))
                .orElse(null);
        assert highestGpaStudent != null;
        System.out.printf("Highest GPA: %.2f (%s)\n", highestGpaStudent.getGpa(), highestGpaStudent.getName());

        Student lowestGpaStudent = students.stream()
                .min(Comparator.comparingDouble(Student::getGpa))
                .orElse(null);
        assert lowestGpaStudent != null;
        System.out.printf("Lowest GPA: %.2f (%s)\n", lowestGpaStudent.getGpa(), lowestGpaStudent.getName());

        double averageAge = students.stream()
                .mapToDouble(Student::getAge)
                .average()
                .orElse(0.0);
        System.out.printf("Average Age: %.2f years\n", averageAge);
    }
}
