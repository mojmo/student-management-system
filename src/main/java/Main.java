import java.util.Scanner;

import services.StudentService;
import utils.AppConfig;

public class Main {
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize app configuration
        AppConfig config = AppConfig.getInstance();
        config.ensureDirectoriesExist();

        int currentOperation;
        char exit = 'n';

        do {
            showMenu();
            try {
                currentOperation = input.nextInt();
            } catch (Exception e) {
                currentOperation = 0;
            }
            input.nextLine();

            switch (currentOperation) {
                case 1 -> StudentService.addStudent(input);
                case 2 -> StudentService.updateStudent(input);
                case 3 -> StudentService.removeStudent(input);
                case 4 -> StudentService.getStudent(input);
                case 5 -> StudentService.getAllStudents();
                case 6 -> StudentService.showStatistics();
                case 7 -> StudentService.exportToPDF();
                case 8 -> exit = 'y';
                default -> System.out.println("Invalid operation. Please try again.\n");
            }
        } while (exit == 'n') ;

        input.close();
    }

    public static void showMenu() {
        System.out.println("""
                
                === Student Management System ===
           
                1. Add Student
                2. Update Student
                3. Remove Student
                4. Search Students
                5. List All Students
                6. Show Statistics
                7. Export to PDF
                8. Save & Exit
                """);

        System.out.print("Select an operation: ");
    }
}