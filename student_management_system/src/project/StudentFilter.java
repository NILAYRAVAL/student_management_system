package project;

import java.util.function.Predicate;

public class StudentFilter {

	public static Predicate<Student> isActive() {
		return student -> student.status() == StudentStatus.ACTIVE;
	}

	public static Predicate<Student> isInactive() {
		return student -> student.status() == StudentStatus.INACTIVE;
	}

	public static Predicate<Student> isGraduated() {
		return student -> student.status() == StudentStatus.GRADUATED;
	}
}
