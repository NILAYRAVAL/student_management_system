package project;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentStatistics {

    public static long countActiveStudents(List<Student> students) {
        return students.stream().filter(StudentFilter.isActive()).count();
    }

    public static Map<StudentStatus, List<Student>> groupStudentsByStatus(List<Student> students) {
        return students.stream().collect(Collectors.groupingBy(Student::status));
    }

    public static Student findOldestStudent(List<Student> students) {
        return students.stream().max((s1, s2) -> Integer.compare(s1.age(), s2.age())).orElse(null);
    }
}
