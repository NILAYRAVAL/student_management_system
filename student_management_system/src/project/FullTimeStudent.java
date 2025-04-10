package project;

import java.time.LocalDate;

public record FullTimeStudent(String name, int age, String studentId, StudentStatus status, LocalDate enrollmentDate) implements Student {

    @Override
    public void printInfo() {
        // Display the full-time student type
        System.out.printf(LanguageSupport.getString("student.info"),
                name, age, studentId, status, enrollmentDate, "Full-Time");
        System.out.println();
    }
}
