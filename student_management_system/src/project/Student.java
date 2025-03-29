package project;

import java.time.LocalDate;

public sealed interface Student permits FullTimeStudent, PartTimeStudent {
    String name();
    int age();
    String studentId();
    StudentStatus status();
    LocalDate enrollmentDate();
    
    void printInfo();
}
