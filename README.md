# College Management System

A web-based College Management System built using Java, Spring Boot, Thymeleaf, and MySQL.

## Features

- Student Management: Add, view, edit, and delete student records.
- Attendance Management: Mark and view attendance.
- Attendance Update: Edit attendance status.
- Duplicate Prevention: Prevents duplicate attendance for the same student on the same date.
- Dashboard: Provides navigation to the main modules.

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven
- HTML and CSS

## Local Setup

1. Install Java and MySQL.
2. Create a MySQL database named `college_management`.
3. Configure your local database credentials in `src/main/resources/application-local.properties`.
4. Run the application using:

   ```bash
   ./mvnw spring-boot:run
   ```

   On Windows, use:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

5. Open `http://localhost:8080` in your browser.

## Security Note

Database credentials are stored in a local configuration file and should not be committed to the public repository.