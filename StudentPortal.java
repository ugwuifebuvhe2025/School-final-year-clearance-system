import java.util.Scanner;

public class StudentPortal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==== FINAL YEAR CLEARANCE STUDENT PORTAL ====");
        System.out.println("1️⃣ Register");
        System.out.println("2️⃣ Login");
        System.out.print("Select an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        switch (choice) {
            case 1 ->                 {
                    System.out.print("Enter full name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter matric number: ");
                    String matric = scanner.nextLine();
                    System.out.print("Enter department: ");
                    String dept = scanner.nextLine();
                    System.out.print("Create a password: ");
                    String pass = scanner.nextLine();
                    Student student = new Student(name, matric, dept, pass);
                    student.registerStudent();
                }
            case 2 ->                 {
                    System.out.print("Enter matric number: ");
                    String matric = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String pass = scanner.nextLine();
                    Student loggedInStudent = Student.login(matric, pass);
                    if (loggedInStudent != null) {
                        loggedInStudent.viewClearanceStatus();
                    }                      }
            default -> System.out.println("❌ Invalid option. Exiting...");
        }
    }
}
