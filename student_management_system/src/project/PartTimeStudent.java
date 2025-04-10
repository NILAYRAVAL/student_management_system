package project;

import java.time.LocalDate;

public record PartTimeStudent(String name, int age, String studentId, StudentStatus status, LocalDate enrollmentDate) implements Student {

    @Override
    public void printInfo() {
        // Display the part-time student type
        System.out.printf(LanguageSupport.getString("student.info"),
                name, age, studentId, status, enrollmentDate, "Part-Time");
        System.out.println();
    }
}
