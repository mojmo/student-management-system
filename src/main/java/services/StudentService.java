package services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import customexceptions.InvalidEmailException;
import customexceptions.InvalidAgeException;
import customexceptions.InvalidGpaException;
import utils.Generators;
import utils.Validator;
import models.Student;
import models.Storage;
import models.FileStorage;

public class StudentService {

    static Storage<Student> storage = new FileStorage<>();
    private static final String MODEL_NAME = "Student";

    // Constants for PDF generation
    private static final float[] TABLE_COLUMNS = {50, 150, 250, 400, 450, 550};
    private static final int FONT_SIZE_TITLE = 16;
    private static final int FONT_SIZE_SECTION = 14;
    private static final int FONT_SIZE_TEXT = 10;
    private static final String PDF_EXPORT_PATH = "src/main/resources/reports/students_report.pdf";

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
        
        String name = promptForName(input);
        String email = promptForEmail(input);
        int age = promptForAge(input);
        String course = promptForCourse(input);
        double gpa = promptForGpa(input);

        String id = "ST" + Generators.generateId();
        Student student = new Student(id, name, email, age, course, gpa);
        saveStudent(student);

        System.out.println("\n***** Student Added Successfully! *****\n");
    }

    private static void saveStudent(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("model", MODEL_NAME);
        map.put("obj", student);
        map.put("fileHeader", Student.FILE_HEADER);
        storage.add(map);
    }

    public static void getStudent(Scanner input) {
        System.out.println("--- Search for Student ---\n");
        String id = promptForId(input);

        String line = storage.get(MODEL_NAME, id);
        if (line.isEmpty()) {
            System.out.println("Student does not exist :(\n");
            return;
        }

        displayStudentHeader();;
        displayStudentDetails(line);
    }

    private static void displayStudentHeader() {
        System.out.printf(
                "%-20s | %-30s | %-30s | %-5s | %-25s | %-5s%n",
                "ID", "NAME", "EMAIL", "AGE", "COURSE", "GPA"
        );
    }

    private static void displayStudentDetails(String line) {
        String[] student = line.split(",");
        System.out.printf(
                "%-20s | %-30s | %-30s | %-5s | %-25s | %-5s\n",
                student[0].trim(),         // id
                student[1].trim(),         // name
                student[2].trim(),         // email
                student[3].trim(),         // age
                student[4].trim(),         // course
                student[5].trim()          // gpa
        );
    }

    public static void removeStudent(Scanner input) {
        System.out.println("--- Remove Student ---\n");
        String id = promptForId(input);

        String line = storage.get(MODEL_NAME, id);
        if (line.isEmpty()) {
            System.out.println("Student does not exist :(\n");
            return;
        }

        System.out.print("Are you sure you want to remove this student? [Y/N]: ");
        if (confirmAction(input)) {
            storage.remove(MODEL_NAME, id);
            System.out.println("\n***** Student Removed Successfully! *****\n");
        }
    }

    private static boolean confirmAction(Scanner input) {
        try {
            String choice = input.nextLine().trim().toUpperCase();
            return choice.equals("Y");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
            return false;
        }
    }

    public static void updateStudent(Scanner input) {
        System.out.println("--- Update Student ---\n");
        String id = promptForId(input);

        String line = storage.get(MODEL_NAME, id);
        if (line.isEmpty()) {
            System.out.println("Student does not exist :(\n");
            return;
        }

        Student oldStudent = parseStudentFromLine(line);
        Student updatedStudent = getUpdatedStudentInfo(input, oldStudent);

        try {
            storage.update(MODEL_NAME, id, updatedStudent);
            System.out.println("\n***** Student Updated Successfully! *****\n");
            System.out.println(updatedStudent);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }

    private static Student parseStudentFromLine(String line) {
        String[] student = line.split(",");
        return new Student(
            student[0].trim(),                       // id
            student[1].trim(),                       // name
            student[2].trim(),                       // email
            Integer.parseInt(student[3].trim()),     // age
            student[4].trim(),                       // course
            Double.parseDouble(student[5].trim())    // gpa
        );
    }

    private static Student getUpdatedStudentInfo(Scanner input, Student oldStudent) {
        System.out.print("Press Enter to skip the field\n");

        String newName = promptForUpdatedField(input, "name", oldStudent.getName());
        String newEmail = promptForUpdatedEmail(input, oldStudent.getEmail());
        int newAge = promptForUpdatedAge(input, oldStudent.getAge());
        String newCourse = promptForUpdatedField(input, "course", oldStudent.getCourse());
        double newGpa = promptForUpdatedGpa(input, oldStudent.getGpa());

        return new Student(oldStudent.getId(), newName, newEmail, newAge, newCourse, newGpa);
    }

    public static void getAllStudents() {
        System.out.println("--- Students List ---\n");
        List<String> students = storage.getAll(MODEL_NAME);

        if (students.isEmpty()) {
            System.out.println("There are no Students :(\n");
            return;
        }

        displayStudentHeader();
        for (String line : students) {
            if (line.startsWith("ID")) continue; // Skip header line
            displayStudentDetails(line); 
        }
    }

    // Helper methods for user input
    private static String promptForName(Scanner input) {
        while (true) {
            System.out.print("Enter your name: ");
            try {
                String name = input.nextLine().trim();
                System.out.println();
                if (Validator.isNotEmpty(name, "Name")) {
                    return name;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static String promptForEmail(Scanner input) {
        while (true) {
            System.out.print("Enter your email: ");
            try {
                String email = input.nextLine().trim();
                System.out.println();
                Validator.isValidEmail(email);
                return email;
            } catch (InvalidEmailException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Please try again.\n");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }
    }

    private static int promptForAge(Scanner input) {
        while (true) {
            System.out.print("Enter your age: ");
            try {
                int age = Integer.parseInt(input.nextLine());
                System.out.println();
                Validator.isValidAge(age);
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Age must be a number!");
            } catch (InvalidAgeException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    private static String promptForCourse(Scanner input) {
        while (true) {
            System.out.print("Enter your course: ");
            String course = input.nextLine().trim();
            System.out.println();
            if (Validator.isValidCourse(course)) {
                return course;
            }
        }
    }

    private static double promptForGpa(Scanner input) {
        while (true) {
            System.out.print("Enter our GPA: ");
            try {
                double gpa = Double.parseDouble(input.nextLine());
                System.out.println();
                Validator.isValidGpa(gpa);
                return gpa;
            } catch (NumberFormatException e) {
                System.out.println("GPA must be a number!\n");
            } catch (InvalidGpaException e) {
                System.out.println(e.getMessage() + "\n");
            }
        }
    }

    private static String promptForId(Scanner input) {
        while (true) {
            System.out.print("Enter student ID:");
            try {
                String id = input.nextLine().trim();
                System.out.println();

                if (id.isEmpty()) {
                    throw new IllegalArgumentException("ID cannot be empty.");
                }
                return id;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
                System.out.println("Please try again.\n");
            }
            
        }
    }

    private static String promptForUpdatedField(Scanner input, String fieldName, String oldValue) {
        System.out.print("Enter new " + fieldName + " (or press Enter to keep current value: " + oldValue + "):");
        String value = input.nextLine().trim();
        System.out.println();
        return value.isEmpty() ? oldValue : value;
    }

    private static String promptForUpdatedEmail(Scanner input, String oldEmail) {
        while (true) {
            System.out.print("Enter new email (or press Enter to keep current email: " + oldEmail + "): ");
            String email = input.nextLine().trim();
            System.out.println();

            if (email.isEmpty()) {
                return oldEmail;
            }

            try {
                Validator.isValidEmail(email);
                return email;
            } catch (InvalidEmailException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Press Enter to keep old email or try again.\n");
            }
        }
    }

    private static int promptForUpdatedAge(Scanner input, int oldAge) {
        while (true) {
            System.out.print("Enter new age (or press Enter to keep current age: " + oldAge + "): ");
            String ageInput = input.nextLine().trim();
            System.out.println();

            if (ageInput.isEmpty()) {
                return oldAge;
            }

            try {
                int age = Integer.parseInt(ageInput);
                Validator.isValidAge(age);
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Age must be a number!");
            } catch (InvalidAgeException e) {
                System.out.println(e.getMessage() + "\n");
                System.out.println("Press Enter to keep old age or try again.\n");
            }
        }
    }

    private static double promptForUpdatedGpa(Scanner input, double oldGpa) {
        while (true) {
            System.out.print("Enter new GPA (or press Enter to keep current GPA: " + oldGpa + "): ");
            String gpaInput = input.nextLine().trim();
            System.out.println();

            if (gpaInput.isEmpty()) {
                return oldGpa;
            }

            try {
                double gpa = Double.parseDouble(gpaInput);
                Validator.isValidGpa(gpa);
                return gpa;
            } catch (NumberFormatException e) {
                System.out.println("GPA must be a number!");
            } catch (InvalidGpaException e) {
                System.out.println(e.getMessage());
                System.out.println("Press Enter to keep old GPA or try again.\n");
            }
        }
    }

    public static void showStatistics() {
        System.out.println("--- Statistics ---\n");
        List<Student> students = getStudentsList();

        if (students.isEmpty()) {
            System.out.println("No students available for statistics.\n");
            return;
        }

        displayBasicStatistics(students);
        displayGpaRangeStatistics(students);
        displayCourseStatistics(students);
    }

    private static List<Student> getStudentsList() {
        List<String> studentLines = storage.getAll(MODEL_NAME);
        return StudentService.deserialize(studentLines);
    }

    private static void displayBasicStatistics(List<Student> students) {
        System.out.println("Number of Students: " + students.size());

        // Average GPA
        double averageGpa = calculateAverageGpa(students);
        System.out.printf("Average GPA: %.2f\n", averageGpa);

        // Highest GPA
        Student highestGpaStudent = findHighestGpaStudent(students);
        System.out.printf(
            "Highest GPA: %.2f (%s)\n",
            highestGpaStudent.getGpa(),
            highestGpaStudent.getName()
        );

        // Lowest GPA
        Student lowestGpaStudent = findLowestGpaStudent(students);
        System.out.printf(
            "Lowest GPA: %.2f\n",
            lowestGpaStudent.getGpa(),
            lowestGpaStudent.getName()
        );

        // Average Age
        double averageAge = calculateAverageAge(students);
        System.out.printf("Average Age: %.1f years\n", averageAge);
    }

    private static double calculateAverageGpa(List<Student> students) {
        return students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    private static Student findHighestGpaStudent(List<Student> students) {
        return students.stream()
                .max(Comparator.comparingDouble(Student::getGpa))
                .orElseThrow(() -> new RuntimeException("No students found"));
    }

    private static Student findLowestGpaStudent(List<Student> students) {
        return students.stream()
                .min(Comparator.comparingDouble(Student::getGpa))
                .orElseThrow(() -> new RuntimeException("No students found"));
    }

    private static double calculateAverageAge(List<Student> students) {
        return students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

    private static void displayGpaRangeStatistics(List<Student> students) {
        System.out.println("\nNumber of students in different GPA ranges:");
        Map<String, Long> gpaRanges = calculateGpaRanges(students);
        gpaRanges.forEach((range, count) -> System.out.printf("%s: %d students\n", range, count));
    }

    private static Map<String, Long> calculateGpaRanges(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(student -> {
                    double gpa = student.getGpa();
                    if (gpa >= 3.5) return "Excellent (3.5 and above)";
                    if (gpa >= 3.0) return "Good (3.0 to 3.5)";
                    if (gpa >= 2.0) return "Average (2.0 to 3.0)";
                    return "Poor (2.0 and below)";
                }, Collectors.counting()));
    }

    private static void displayCourseStatistics(List<Student> students) {
        System.out.println("\n--- Course-wise Distribution ---\n");

        // Students per course
        Map<String, Long> courseCounts = getStudentsPerCourse(students);
        courseCounts.forEach((course, count) -> 
            System.out.printf("%s: %d students\n", course, count)
        );

        // Average GPA by course
        System.out.println("\nAverage GPA by Course:");
        Map<String, Double> courseGpa = getAverageGpaPerCourse(students);
        courseGpa.forEach((course, gpa) -> 
            System.out.printf("%s: %.2f\n", course, gpa)
        );
    }

    private static Map<String, Long> getStudentsPerCourse(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getCourse, Collectors.counting()));
    }

    public static Map<String, Double> getAverageGpaPerCourse(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getCourse,
                        Collectors.averagingDouble(Student::getGpa)));
    }

    public static void exportToPDF() {
        List<Student> students = getStudentsList();

        if (students.isEmpty()) {
            System.out.println("No students available to export.");
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Start with initial Y position at the top of the page
                float yPosition = 750;
                
                // Draw PDF content with dynamically updated Y position
                yPosition = addPdfTitle(contentStream, yPosition);
                yPosition = addPdfStudentTable(contentStream, students, yPosition);
                addPdfStatistics(contentStream, students, yPosition);
            }

            // Save PDF
            savePdfDocument(document);
        } catch (IOException e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }

    private static float addPdfTitle(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_TITLE);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Students Report");
        contentStream.endText();
        
        // Return the new Y position after adding title and some spacing
        return yPosition - 30;
    }

    private static float addPdfStudentTable(PDPageContentStream contentStream, List<Student> students, float yPosition) throws IOException {
        // Student section title
        contentStream.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_SECTION);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Students List");
        contentStream.endText();

        // Space after section title
        yPosition -= 20;

        // Draw table header
        drawPdfTableHeader(contentStream, yPosition);
        yPosition -= 20;

        // Draw student rows and get updated Y position
        return drawPdfTableRows(contentStream, students, yPosition);
    }

    private static void drawPdfTableHeader(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_TEXT);
        contentStream.beginText();
        contentStream.newLineAtOffset(TABLE_COLUMNS[0], yPosition);
        contentStream.showText("ID");
        contentStream.newLineAtOffset(TABLE_COLUMNS[1] - TABLE_COLUMNS[0], 0);
        contentStream.showText("NAME");
        contentStream.newLineAtOffset(TABLE_COLUMNS[2] - TABLE_COLUMNS[1], 0);
        contentStream.showText("EMAIL");
        contentStream.newLineAtOffset(TABLE_COLUMNS[3] - TABLE_COLUMNS[2], 0);
        contentStream.showText("AGE");
        contentStream.newLineAtOffset(TABLE_COLUMNS[4] - TABLE_COLUMNS[3], 0);
        contentStream.showText("COURSE");
        contentStream.newLineAtOffset(TABLE_COLUMNS[5] - TABLE_COLUMNS[4], 0);
        contentStream.showText("GPA");
        contentStream.endText();

        // Header line
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(TABLE_COLUMNS[0], yPosition - 5);
        contentStream.lineTo(TABLE_COLUMNS[5] + 50, yPosition - 5);
        contentStream.stroke();
    }

    private static float drawPdfTableRows(PDPageContentStream contentStream, List<Student> students, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.TIMES_ROMAN, FONT_SIZE_TEXT);
        float rowHeight = 20;
        int maxRowsPerPage = 25; // Prevents overflow to bottom of page
        int rowCount = 0;

        for (Student student : students) {
            contentStream.beginText();
            contentStream.newLineAtOffset(TABLE_COLUMNS[0], yPosition);
            contentStream.showText(student.getId());
            contentStream.newLineAtOffset(TABLE_COLUMNS[1] - TABLE_COLUMNS[0], 0);
            contentStream.showText(student.getName());
            contentStream.newLineAtOffset(TABLE_COLUMNS[2] - TABLE_COLUMNS[1], 0);
            contentStream.showText(student.getEmail());
            contentStream.newLineAtOffset(TABLE_COLUMNS[3] - TABLE_COLUMNS[2], 0);
            contentStream.showText(String.valueOf(student.getAge()));
            contentStream.newLineAtOffset(TABLE_COLUMNS[4] - TABLE_COLUMNS[3], 0);
            contentStream.showText(student.getCourse());
            contentStream.newLineAtOffset(TABLE_COLUMNS[5] - TABLE_COLUMNS[4], 0);
            contentStream.showText(String.format("%.2f", student.getGpa()));
            contentStream.endText();
            
            yPosition -= rowHeight;
            rowCount++;
            
            // Check if we need a new page
            if (rowCount >= maxRowsPerPage || yPosition < 100) {
                // In a more advanced implementation, we would add a new page here
                // and continue rendering the remaining students
                break;
            }
        }
        
        // Add extra spacing after the table
        return yPosition - 20;
    }

    private static void addPdfStatistics(PDPageContentStream contentStream, List<Student> students, float yPosition) throws IOException {
        // Check if we need to start statistics on a new page
        if (yPosition < 200) {
            // In a more advanced implementation, we would add a new page here
            // For now, we'll just abort adding statistics if there's not enough space
            System.out.println("Not enough space for statistics. Consider implementing pagination.");
            return;
        }

        // Statistics title
        contentStream.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_SECTION);
        contentStream.beginText();
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Statistics");
        yPosition -= 20;

        // Basic statistics
        contentStream.setFont(PDType1Font.TIMES_BOLD, FONT_SIZE_TEXT);
        
        // Student count
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Number of Students: " + students.size());
        
        // Average GPA
        double averageGpa = calculateAverageGpa(students);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(String.format("Average GPA: %.2f", averageGpa));
        
        // Highest GPA
        Student highestGpaStudent = findHighestGpaStudent(students);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(String.format(
            "Highest GPA: %.2f (%s)",
            highestGpaStudent.getGpa(),
            highestGpaStudent.getName()
        ));

        // Lowest GPA
        Student lowestGpaStudent = findLowestGpaStudent(students);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(String.format(
            "Lowest GPA: %.2f (%s)",
            lowestGpaStudent.getGpa(),
            lowestGpaStudent.getName()
        ));

        // Average Age
        double averageAge = calculateAverageAge(students);
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText(String.format("Average Age: %.1f years", averageAge));
        
        // GPA Ranges header
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Number of students in different GPA ranges:");

        // GPA Ranges data
        Map<String, Long> gpaRanges = calculateGpaRanges(students);
        for (Map.Entry<String, Long> entry : gpaRanges.entrySet()) {
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(String.format("%s: %d students",
                entry.getKey(), entry.getValue()
            ));
        }
        
        contentStream.endText();
    }
    

    private static void savePdfDocument(PDDocument document) throws IOException {
        File directory = new File("src/main/resources/reports");
        if (!directory.exists() && directory.mkdirs()) {
            System.out.println("Created reports directory.");
        }

        document.save(PDF_EXPORT_PATH);
        System.out.println("PDF exported successfully to: " + PDF_EXPORT_PATH);
    }

    /**
     * Imports students from a CSV file using batch add
     * 
     * @param input Scanner for user input
     */
    public static void importStudentsFromCSV(Scanner input) {
        System.out.println("--- Import Students from CSV ---\n");
        System.out.print("Enter the path to CSV file: ");
        String filePath = input.nextLine().trim();
        
        if (filePath.isEmpty()) {
            System.out.println("File path cannot be empty");
            return;
        }
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            
            // Skip header if exists
            if (lines.isEmpty()) {
                System.out.println("File is empty");
                return;
            }
            
            boolean hasHeader = lines.get(0).startsWith("ID") || 
                                lines.get(0).toLowerCase().contains("name") ||
                                lines.get(0).toLowerCase().contains("email");
            int startIndex = hasHeader ? 1 : 0;
            
            List<Student> students = new ArrayList<>();
            List<String> errorLines = new ArrayList<>();
            
            for (int i = startIndex; i < lines.size(); i++) {
                String line = lines.get(i);
                try {
                    String[] fields = line.split(",");
                    if (fields.length < 6) {
                        errorLines.add("Line " + (i+1) + ": Insufficient columns");
                        continue;
                    }
                    
                    // Generate new ID for each student
                    String id = "ST" + Generators.generateId();
                    String name = fields[0].trim();
                    String email = fields[1].trim();
                    int age;
                    String course = fields[3].trim();
                    double gpa;
                    
                    try {
                        age = Integer.parseInt(fields[2].trim());
                        Validator.isValidAge(age);
                    } catch (Exception e) {
                        errorLines.add("Line " + (i+1) + ": Invalid age - " + fields[2]);
                        continue;
                    }
                    
                    try {
                        gpa = Double.parseDouble(fields[4].trim());
                        Validator.isValidGpa(gpa);
                    } catch (Exception e) {
                        errorLines.add("Line " + (i+1) + ": Invalid GPA - " + fields[4]);
                        continue;
                    }
                    
                    try {
                        Validator.isValidEmail(email);
                    } catch (InvalidEmailException e) {
                        errorLines.add("Line " + (i+1) + ": Invalid email - " + email);
                        continue;
                    }
                    
                    Student student = new Student(id, name, email, age, course, gpa);
                    students.add(student);
                    
                } catch (Exception e) {
                    errorLines.add("Line " + (i+1) + ": " + e.getMessage());
                }
            }
            
            if (students.isEmpty()) {
                System.out.println("No valid students found for import");
                return;
            }
            
            // Use batch add for better performance
            int addedCount = addStudentsBatch(students);
            
            System.out.println("\n***** Import Summary *****");
            System.out.println("Students successfully imported: " + addedCount);
            
            if (!errorLines.isEmpty()) {
                System.out.println("Errors encountered: " + errorLines.size());
                System.out.println("First 5 errors:");
                for (int i = 0; i < Math.min(5, errorLines.size()); i++) {
                    System.out.println("  - " + errorLines.get(i));
                }
                
                if (errorLines.size() > 5) {
                    System.out.println("  ... and " + (errorLines.size() - 5) + " more errors");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Adds multiple students at once using batch operation for better performance
     * 
     * @param students List of students to add
     * @return Number of students successfully added
     */
    public static int addStudentsBatch(List<Student> students) {
        if (students == null || students.isEmpty()) {
            return 0;
        }
        
        try {
            storage.batchAdd(MODEL_NAME, students, Student.FILE_HEADER);
            return students.size();
        } catch (Exception e) {
            System.err.println("Error during batch add: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Updates multiple students at once using batch operation for better performance
     * 
     * @param studentMap Map of student IDs to updated student objects
     * @return Number of students successfully updated
     */
    public static int updateStudentsBatch(Map<String, Student> studentMap) {
        if (studentMap == null || studentMap.isEmpty()) {
            return 0;
        }
        
        try {
            storage.batchUpdate(MODEL_NAME, studentMap);
            return studentMap.size();
        } catch (Exception e) {
            System.err.println("Error during batch update: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Updates GPAs for students in a specific course by a scaling factor
     * 
     * @param input Scanner for user input
     */
    public static void batchUpdateGpas(Scanner input) {
        System.out.println("--- Batch GPA Update ---\n");
        
        List<Student> allStudents = getStudentsList();
        if (allStudents.isEmpty()) {
            System.out.println("No students available to update");
            return;
        }
        
        // Display available courses
        Set<String> courses = allStudents.stream()
                .map(Student::getCourse)
                .collect(Collectors.toSet());
        
        System.out.println("Available courses:");
        courses.forEach(course -> System.out.println("- " + course));
        
        // Get course to update
        System.out.print("\nEnter course name for GPA adjustment: ");
        String courseName = input.nextLine().trim();
        
        if (courseName.isEmpty() || !courses.contains(courseName)) {
            System.out.println("Invalid or non-existent course name");
            return;
        }
        
        // Get adjustment factor
        System.out.print("Enter GPA adjustment factor (e.g., 1.1 for 10% increase): ");
        double factor;
        try {
            factor = Double.parseDouble(input.nextLine().trim());
            if (factor <= 0) {
                System.out.println("Adjustment factor must be positive");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format");
            return;
        }
        
        // Filter students by course and update GPAs
        Map<String, Student> updatedStudents = new HashMap<>();
        
        for (Student student : allStudents) {
            if (student.getCourse().equals(courseName)) {
                double newGpa = student.getGpa() * factor;
                // Cap at max GPA value
                newGpa = Math.min(newGpa, 4.0);
                
                Student updatedStudent = new Student(
                    student.getId(),
                    student.getName(),
                    student.getEmail(),
                    student.getAge(),
                    student.getCourse(),
                    newGpa
                );
                
                updatedStudents.put(student.getId(), updatedStudent);
            }
        }
        
        if (updatedStudents.isEmpty()) {
            System.out.println("No students found in course: " + courseName);
            return;
        }
        
        // Confirm operation
        System.out.println("\nThis will update GPAs for " + updatedStudents.size() + 
                          " students in " + courseName + " course.");
        System.out.print("Are you sure? [Y/N]: ");
        if (confirmAction(input)) {
            // Use batch update for better performance
            int updatedCount = updateStudentsBatch(updatedStudents);
            System.out.println("\n***** Successfully updated GPAs for " + updatedCount + " students *****");
        } else {
            System.out.println("\nOperation cancelled.");
        }
    }
}
