package project;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        Scanner scanner = new Scanner(System.in);

        // Set default language to English
        LanguageSupport.setLanguage("en");

        while (true) {
            System.out.println(
                    "\nEnter command: add, update, remove, display, stats, updateStatus, process, lang, sort, filter, exit");
            String command = scanner.nextLine();

            switch (command) {
                case "add" -> manager.addStudent();
                case "update" -> manager.updateStudent();
                case "remove" -> manager.removeStudent();
                case "display" -> manager.showAllStudents();
                case "stats" -> manager.showStatistics();
                case "updateStatus" -> manager.updateStudentStatus();
                case "process" -> manager.processStudentsConcurrently();
                case "lang" -> {
                    System.out.print("Enter language code (en/es/ga): ");
                    String lang = scanner.nextLine();
                    LanguageSupport.setLanguage(lang);
                    System.out.println("Language set to " + lang);
                }
                case "sort" -> {
                    System.out.print("Sort by (name/age/studentId): ");
                    String attribute = scanner.nextLine();
                    manager.sortStudents(attribute);
                }
                case "filter" -> {
                    System.out.print("Filter by status (ACTIVE/INACTIVE/GRADUATED): ");
                    String status = scanner.nextLine();
                    manager.filterStudents(status);
                }
                case "exit" -> {
                    System.out.println(LanguageSupport.getString("message.exit"));
                    scanner.close();
                    return;
                }
                default -> System.out.println(LanguageSupport.getString("error.invalidCommand"));
            }
        }
    }
}
