package project;

import java.time.LocalDate;

public record PartTimeStudent(String name, int age, String studentId, StudentStatus status, LocalDate enrollmentDate) implements Student {
    @Override
    public void printInfo() {
        System.out.printf(LanguageSupport.getString("student.info"),
                name, age, studentId, status, enrollmentDate);
        System.out.println();
    }
}
