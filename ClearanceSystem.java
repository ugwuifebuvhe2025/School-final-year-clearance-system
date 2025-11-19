import java.util.Scanner;

public class ClearanceSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==== FINAL YEAR CLEARANCE SYSTEM ====");

        // --- Admin login ---
        Admin admin = new Admin();
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (admin.login(username, password)) {
            admin.showMenu();
        } else {
            System.out.println("Access denied!");
        }
        
    }
}


