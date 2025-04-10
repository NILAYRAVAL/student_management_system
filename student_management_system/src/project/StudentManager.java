package project;

import java.time.LocalDate;
import java.util.*;
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
    public void updateStudent() {
        System.out.print(LanguageSupport.getString("prompt.studentIdToUpdate"));
        String studentId = scanner.nextLine();

        for (Student student : students) {
            if (student.studentId().equals(studentId)) {
                System.out.print(LanguageSupport.getString("prompt.newName") + " (current: " + student.name() + "): ");
                String newName = scanner.nextLine();
                System.out.print(LanguageSupport.getString("prompt.newAge") + " (current: " + student.age() + "): ");
                int newAge = Integer.parseInt(scanner.nextLine());

                Student updatedStudent = switch (student) {
                    case FullTimeStudent ft -> new FullTimeStudent(
                        newName.isEmpty() ? ft.name() : newName, 
                        newAge <= 0 ? ft.age() : newAge, 
                        ft.studentId(), 
                        ft.status(), 
                        ft.enrollmentDate()
                    );
                    case PartTimeStudent pt -> new PartTimeStudent(
                        newName.isEmpty() ? pt.name() : newName, 
                        newAge <= 0 ? pt.age() : newAge, 
                        pt.studentId(), 
                        pt.status(), 
                        pt.enrollmentDate()
                    );
                    default -> student; // Fallback, shouldn't happen with current model
                };

                students.remove(student);
                students.add(updatedStudent);
                System.out.println(LanguageSupport.getString("message.studentUpdated"));
                return;
            }
        }
        System.out.println(LanguageSupport.getString("message.studentNotFound"));
    }


  

       public void removeStudent() {
        System.out.print("Enter student ID to remove: ");
        String studentId = scanner.nextLine();

        Iterator<Student> iterator = students.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            if (student.studentId().equals(studentId)) {
                iterator.remove();
                System.out.println("Student with ID " + studentId + " has been removed.");
                return;
            }
        }

        System.out.println("Student with ID " + studentId + " not found.");
    }
       public void showAllStudents() {
           if (students.isEmpty()) {
               System.out.println(LanguageSupport.getString("message.noStudents"));
           } else {
               System.out.println(LanguageSupport.getString("message.studentsList"));
               students.forEach(Student::printInfo);
           }
       }
    public void showStatistics() {
        long activeCount = students.stream().filter(s -> s.status() == StudentStatus.ACTIVE).count();
        long inactiveCount = students.stream().filter(s -> s.status() == StudentStatus.INACTIVE).count();
        long graduatedCount = students.stream().filter(s -> s.status() == StudentStatus.GRADUATED).count();

        System.out.println("Student Statistics:");
        System.out.println("Active students: " + activeCount);
        System.out.println("Inactive students: " + inactiveCount);
        System.out.println("Graduated students: " + graduatedCount);
    }

    public void updateStudentStatus() {
        System.out.print("Enter student ID to update status: ");
        String studentId = scanner.nextLine();

        for (Student student : students) {
            if (student.studentId().equals(studentId)) {
                System.out.print("Enter new status (ACTIVE/INACTIVE/GRADUATED): ");
                String status = scanner.nextLine().toUpperCase();

                try {
                    StudentStatus newStatus = StudentStatus.valueOf(status);
                    Student updatedStudent = switch (student) {
                        case FullTimeStudent ft -> new FullTimeStudent(ft.name(), ft.age(), ft.studentId(), newStatus, ft.enrollmentDate());
                        case PartTimeStudent pt -> new PartTimeStudent(pt.name(), pt.age(), pt.studentId(), newStatus, pt.enrollmentDate());
                        default -> student;
                    };

                    students.remove(student);
                    students.add(updatedStudent);
                    System.out.println("Status updated for student with ID " + studentId);
                    return;
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status entered.");
                }
            }
        }

        System.out.println("Student with ID " + studentId + " not found.");
    }

    public void sortStudents(String attribute) {
        switch (attribute.toLowerCase()) {
            case "name" -> students.sort(Comparator.comparing(Student::name));
            case "age" -> students.sort(Comparator.comparingInt(Student::age));
            case "studentid" -> students.sort(Comparator.comparing(Student::studentId));
            default -> System.out.println("Invalid attribute for sorting.");
        }

        showAllStudents(); // Display sorted students
    }

    public void filterStudents(String criteria) {
        switch (criteria.toLowerCase()) {
            case "active":
                students.stream().filter(StudentFilter.isActive()).forEach(Student::printInfo);
                break;
            case "inactive":
                students.stream().filter(StudentFilter.isInactive()).forEach(Student::printInfo);
                break;
            case "graduated":
                students.stream().filter(StudentFilter.isGraduated()).forEach(Student::printInfo);
                break;
            case "fulltime":
                students.stream().filter(StudentFilter.isFullTime()).forEach(Student::printInfo);
                break;
            case "parttime":
                students.stream().filter(StudentFilter.isPartTime()).forEach(Student::printInfo);
                break;
            default:
                System.out.println(LanguageSupport.getString("error.invalidFilter"));
                break;
        }
    }
}
