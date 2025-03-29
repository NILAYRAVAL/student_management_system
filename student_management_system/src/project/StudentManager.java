package project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StudentManager {

    private final List<Student> students = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[A-Z]\\d{8}$");

    public void addStudent() {
        System.out.print(LanguageSupport.getString("prompt.name"));
        String name = scanner.nextLine();
        System.out.print(LanguageSupport.getString("prompt.age"));
        int age = Integer.parseInt(scanner.nextLine());
        String studentId;

        while (true) {
            System.out.print(LanguageSupport.getString("prompt.studentId"));
            studentId = scanner.nextLine();
            if (STUDENT_ID_PATTERN.matcher(studentId).matches()) {
                break;
            } else {
                System.out.println(LanguageSupport.getString("error.invalidStudentId"));
            }
        }

        System.out.print("Is the student Full-Time or Part-Time? (full/part): ");
        String type = scanner.nextLine().toLowerCase();
        Student student;
        if (type.equals("full")) {
            student = new FullTimeStudent(name, age, studentId, StudentStatus.ACTIVE, LocalDate.now());
        } else {
            student = new PartTimeStudent(name, age, studentId, StudentStatus.ACTIVE, LocalDate.now());
        }

        students.add(student);
        System.out.println(LanguageSupport.getString("message.studentAdded"));
    }

    public void showAllStudents() {
        if (students.isEmpty()) {
            System.out.println(LanguageSupport.getString("message.noStudents"));
        } else {
            System.out.println(LanguageSupport.getString("message.studentsList"));
            students.forEach(Student::printInfo);
        }
    }

    public void updateStudent() {
        System.out.print(LanguageSupport.getString("prompt.studentIdToUpdate"));
        String studentId = scanner.nextLine();

        for (Student student : students) {
            if (student.studentId().equals(studentId)) {
                System.out.print(LanguageSupport.getString("prompt.newName") + " (current: " + student.name() + "): ");
                String newName = scanner.nextLine();
                System.out.print(LanguageSupport.getString("prompt.newAge") + " (current: " + student.age() + "): ");
                int newAge = Integer.parseInt(scanner.nextLine());

                Student updatedStudent = student instanceof FullTimeStudent
                        ? new FullTimeStudent(newName.isEmpty() ? student.name() : newName,
                        newAge <= 0 ? student.age() : newAge, student.studentId(),
                        student.status(), student.enrollmentDate())
                        : new PartTimeStudent(newName.isEmpty() ? student.name() : newName,
                        newAge <= 0 ? student.age() : newAge, student.studentId(),
                        student.status(), student.enrollmentDate());

                students.remove(student);
                students.add(updatedStudent);
                System.out.println(LanguageSupport.getString("message.studentUpdated"));
                return;
            }
        }
        System.out.println(LanguageSupport.getString("message.studentNotFound"));
    }

    public void removeStudent() {
        System.out.print(LanguageSupport.getString("prompt.studentIdToRemove"));
        String studentId = scanner.nextLine();
        students.removeIf(student -> student.studentId().equals(studentId));
        System.out.println(LanguageSupport.getString("message.studentRemoved"));
    }

    public void updateStudentStatus() {
        System.out.print(LanguageSupport.getString("prompt.studentIdToUpdateStatus"));
        String studentId = scanner.nextLine();

        for (Student student : students) {
            if (student.studentId().equals(studentId)) {
                System.out.print(LanguageSupport.getString("prompt.newStatus") + " (ACTIVE, INACTIVE, GRADUATED): ");
                String newStatus = scanner.nextLine().toUpperCase();

                try {
                    StudentStatus status = StudentStatus.valueOf(newStatus);
                    Student updatedStudent = student instanceof FullTimeStudent
                            ? new FullTimeStudent(student.name(), student.age(), student.studentId(), status,
                            student.enrollmentDate())
                            : new PartTimeStudent(student.name(), student.age(), student.studentId(), status,
                            student.enrollmentDate());

                    students.remove(student);
                    students.add(updatedStudent);
                    System.out.println(LanguageSupport.getString("message.statusUpdated"));
                } catch (IllegalArgumentException e) {
                    System.out.println(LanguageSupport.getString("error.invalidStatus"));
                }
                return;
            }
        }
        System.out.println(LanguageSupport.getString("message.studentNotFound"));
    }

    public void showStatistics() {
        long activeCount = StudentStatistics.countActiveStudents(students);
        System.out.println(LanguageSupport.getString("message.totalActiveStudents") + activeCount);

        var groupedStudents = StudentStatistics.groupStudentsByStatus(students);
        groupedStudents.forEach((status, studentList) -> {
            System.out.println(status + ":");
            studentList.forEach(Student::printInfo);
        });

        Student oldestStudent = StudentStatistics.findOldestStudent(students);
        if (oldestStudent != null) {
            System.out.println(LanguageSupport.getString("message.oldestStudent"));
            oldestStudent.printInfo();
        } else {
            System.out.println(LanguageSupport.getString("message.noStudentsAvailable"));
        }
    }

    public void processStudentsConcurrently() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        students.forEach(student -> executor.submit(student::printInfo));
        executor.shutdown();
    }

    public void sortStudents(String attribute) {
        Comparator<Student> comparator;
        switch (attribute) {
            case "name" -> comparator = Comparator.comparing(Student::name);
            case "age" -> comparator = Comparator.comparingInt(Student::age);
            case "studentId" -> comparator = Comparator.comparing(Student::studentId);
            default -> {
                System.out.println(LanguageSupport.getString("error.invalidSortAttribute"));
                return;
            }
        }

        students.sort(comparator);
        System.out.println(LanguageSupport.getString("message.studentsSorted"));
        showAllStudents();
    }

    public void filterStudents(String status) {
        try {
            StudentStatus studentStatus = StudentStatus.valueOf(status.toUpperCase());
            List<Student> filteredStudents = students.stream()
                    .filter(student -> student.status() == studentStatus)
                    .collect(Collectors.toList());

            if (filteredStudents.isEmpty()) {
                System.out.println(LanguageSupport.getString("message.noStudents"));
            } else {
                System.out.println(LanguageSupport.getString("message.studentsList"));
                filteredStudents.forEach(Student::printInfo);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(LanguageSupport.getString("error.invalidStatus"));
        }
    }

    public void saveStudentsToFile(String filename) {
        Path path = Paths.get(filename);
        try {
            Files.write(path, students.stream()
                    .map(student -> String.join(",", student.name(), String.valueOf(student.age()), student.studentId(), student.status().toString(), student.enrollmentDate().toString()))
                    .collect(Collectors.toList()));
            System.out.println("Students saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving students to file: " + e.getMessage());
        }
    }

    public void loadStudentsFromFile(String filename) {
        Path path = Paths.get(filename);
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                String name = parts[0];
                int age = Integer.parseInt(parts[1]);
                String studentId = parts[2];
                StudentStatus status = StudentStatus.valueOf(parts[3]);
                LocalDate enrollmentDate = LocalDate.parse(parts[4]);

                Student student = (status == StudentStatus.ACTIVE) 
                        ? new FullTimeStudent(name, age, studentId, status, enrollmentDate) 
                        : new PartTimeStudent(name, age, studentId, status, enrollmentDate);

                students.add(student);
            }
            System.out.println("Students loaded from file: " + filename);
        } catch (IOException e) {
            System.out.println("Error loading students from file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing student data: " + e.getMessage());
        }
    }
}
