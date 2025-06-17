# Student Management System

![Java Version](https://img.shields.io/badge/Java-21-orange)
![CLI](https://img.shields.io/badge/UI-CLI-yellow)  
![License](https://img.shields.io/badge/License-MIT-blue)
![Version](https://img.shields.io/badge/Version-1.0-green)

A robust Java-based command-line application for comprehensive student record management with advanced features including data validation, batch operations, and reporting.

## Overview

The Student Management System provides educational institutions with a powerful yet simple tool to manage student data. This console application offers intuitive commands for adding, updating, and analyzing student information while ensuring data integrity and offering performance optimizations for larger datasets.

## Features

### Core Functionality
- **Student Records Management**: Add, update, search, and remove student profiles
- **Data Validation**: Comprehensive validation for all input fields (email format, age range, GPA limits)
- **Email Uniqueness**: Prevents duplicate email addresses across all operations
- **Batch Operations**: Efficiently process multiple records at once
- **Statistics**: Generate insights about student performance and demographics

### Advanced Features
- **PDF Reporting**: Export student data and statistics to professionally formatted PDF documents
- **CSV Import**: Bulk import student records from CSV files with validation
- **Batch GPA Updates**: Apply GPA adjustments to students by course
- **Data Persistence**: All data is stored in CSV format for easy access and portability

## System Requirements

- Java 21 or higher
- Maven for dependency management
- PDFBox 2.0.27 for PDF generation

## Installation

1. Clone the repository:
```bash
git clone https://github.com/mojmo/student-management-system.git
cd student-management-system
```

2. Build the project with Maven:
```bash
mvn clean package
```

3. Run the application:
```bash
java -cp target/student-management-system-1.0.jar Main
```

## Usage

The system provides a menu-driven interface with the following options:

```
=== Student Management System ===
           
1. Add Student
2. Update Student
3. Remove Student
4. Search Students
5. List All Students
6. Show Statistics
7. Export to PDF
8. Import Students from CSV
9. Batch Update GPAs
0. Save & Exit
```

### Adding a Student
Enter student details including name, email, age, course, and GPA. The system validates all inputs and ensures email uniqueness.

### Importing Students from CSV
Import multiple students from a CSV file. The expected format is:
```
Name,Email,Age,Course,GPA
John Doe,john@example.com,21,Computer Science,3.8
```

### Batch Updating GPAs
Apply a scaling factor to GPA scores for all students in a specific course, allowing for course-wide adjustments.

### Exporting to PDF
Generate a comprehensive PDF report containing student data and statistics, including:
- Complete student roster
- GPA statistics (average, highest, lowest)
- Age demographics
- Distribution by GPA ranges
- Course enrollment statistics

## Architecture

The application is built using a layered architecture:

- **Models**: Data structures for students and storage interfaces
- **Services**: Business logic and operations processing
- **Utils**: Helper classes for validation and configuration
- **Custom Exceptions**: Specialized error handling

### Data Storage

Student data is stored in CSV format with the following structure:
```
ID,NAME,EMAIL,AGE,COURSE,GPA
ST1749719388606,John Doe,john@example.com,23,Software Engineering,3.62
```

Files are located in the `src/main/resources/data` directory by default.

## Performance Optimizations

- **Buffered I/O**: Uses buffered readers/writers for improved file operations
- **Batch Processing**: Minimizes disk I/O for multiple operations
- **Storage Configuration**: Configurable storage paths and formats
- **Dynamic PDF Generation**: Efficient memory usage for report generation

## Future Enhancements

- User authentication and role-based access control
- Database integration options (SQL or NoSQL)
- Web interface with Spring Boot
- Enhanced reporting and analytics
- Pagination for PDF reports with large datasets
- Course management functionality

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a pull request

Please ensure your code follows the project's coding conventions and includes appropriate tests.

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## Acknowledgements

- Apache PDFBox for PDF generation capabilities
- Java Stream API for efficient data processing
- Maven for dependency management

---

Developed by Mugtaba Mohamed © 2025
