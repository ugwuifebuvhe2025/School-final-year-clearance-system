import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Admin {
    private int id;
    private String username;
    private String unitName;
    private String matric_no;

    public boolean login(String username, String password) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.id = rs.getInt("id");
                this.username = rs.getString("username");
                this.unitName = rs.getString("unit_name");
                System.out.println("\n✅ Login successful! Logged in as: " + unitName + " Admin");
                return true;
            } else {
                System.out.println("❌ Invalid username or password!");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Database error during login: " + e.getMessage());
            return false;
        }
    }

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n==== " + unitName.toUpperCase() + " ADMIN DASHBOARD ====");
            System.out.println("1️⃣  Clear a Student");
            System.out.println("2️⃣  View Cleared Students");
            if (unitName.equalsIgnoreCase("Registry")) {
                System.out.println("3️⃣  Generate Final Clearance Certificate");
            }
            System.out.println("0️⃣  Logout");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> clearStudent(scanner);
                case 2 -> viewClearedStudents();
                case 3 -> {
                    if (unitName.equalsIgnoreCase("Registry")) {
                        generateCertificate(scanner);
                    } else {
                        System.out.println("⚠️ Only Registry Admin can generate certificates.");
                    }
                }
                case 0 -> {
                    System.out.println("👋 Logged out successfully!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice, try again.");
            }
        }
    }

    private void clearStudent(Scanner scanner) {
        System.out.print("Enter student matric number: ");
        String matric = scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // check if student exists
            PreparedStatement check = conn.prepareStatement("SELECT * FROM students WHERE matric_no = ?");
            check.setString(1, matric);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Student not found in database!");
                return;
            }

            // record clearance
            String sql = "INSERT INTO clearances (matric_no, unit_name, cleared_at) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, matric);
            stmt.setString(2, unitName);
            stmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.executeUpdate();

            System.out.println("✅ " + unitName + " has cleared student: " + matric);
        } catch (SQLException e) {
            System.out.println("⚠️ Error during clearance: " + e.getMessage());
        }
    }

    private void viewClearedStudents() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT matric_no, cleared_at FROM clearances WHERE unit_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, unitName);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n📋 Students Cleared by " + unitName + ":");
            boolean found = false;
            while (rs.next()) {
                System.out.println("- " + rs.getString("matric_no") + " | Cleared at: " + rs.getString("cleared_at"));
                found = true;
            }
            if (!found) System.out.println("⚠️ No students cleared yet.");
        } catch (SQLException e) {
            System.out.println("⚠️ Error viewing cleared students: " + e.getMessage());
        }
    }

    private void generateCertificate(Scanner scanner) {
        System.out.print("Enter student matric number to check clearance: ");
        String matric = scanner.nextLine();

        Student student = Student.findByMatric(matric);
        if (student == null) {
            System.out.println("❌ Student not found!");
            return;
        }

        Registry registry = new Registry();
        if (registry.allCleared(student)) {
            registry.generateCertificate(student);
        } else {
            System.out.println("⚠️ Not all units have cleared this student yet.");
        }
    }

    public String getUnitName() {
        return unitName;
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }
    
}
