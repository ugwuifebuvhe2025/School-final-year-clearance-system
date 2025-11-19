import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Student {

    static Student getStudentById(int studentId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
     String name;
     String matricNo;
     String department;
     String password;
     int id;
     String faculty;

    public Student(String name, String matricNo, String department, String password) {
        this.name = name;
        this.matricNo = matricNo;
        this.department = department;
        this.password = password;
        this.id = id;
        this.faculty = faculty;
    }

    public void registerStudent() {
        String sql = "INSERT INTO students (name, matric_no, department, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, matricNo);
            stmt.setString(3, department);
            stmt.setString(4, password);
            stmt.executeUpdate();

            System.out.println("✅ Student registered successfully!");
        } catch (SQLException e) {
            System.out.println("⚠️ Error registering student: " + e.getMessage());
        }
    }

    public static Student login(String matricNo, String password) {
        String sql = "SELECT * FROM students WHERE matric_no = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricNo);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Login successful! Welcome " + rs.getString("name"));
                return new Student(
                    rs.getString("name"),
                    rs.getString("matric_no"),
                    rs.getString("department"),
                    rs.getString("password"));
            } else {
                System.out.println("❌ Invalid matric number or password.");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Database error during login: " + e.getMessage());
            return null;
        }
    }

    public void viewClearanceStatus() {
        String[] units = {"Library", "Hostel", "Medical", "Bursary", "Faculty", "Exam", "Security"};
        List<String> clearedUnits = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT unit_name FROM clearances WHERE matric_no = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, matricNo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                clearedUnits.add(rs.getString("unit_name"));
            }

            System.out.println("\n==== CLEARANCE STATUS FOR " + name.toUpperCase() + " ====");
            for (String unit : units) {
                if (clearedUnits.contains(unit)) {
                    System.out.println("✅ " + unit + " - Cleared");
                } else {
                    System.out.println("❌ " + unit + " - Pending");
                }
            }

            if (clearedUnits.size() == units.length) {
                System.out.println("\n🎓 All units cleared! You can now download your clearance certificate.");
            } else {
                System.out.println("\n⚠️ Some units have not yet cleared you.");
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Error fetching clearance status: " + e.getMessage());
        }
    }

    // Utility method for Registry/Admin to find student
    public static Student findByMatric(String matricNo) {
        String sql = "SELECT * FROM students WHERE matric_no = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, matricNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Student(
                    rs.getString("name"),
                    rs.getString("matric_no"),
                    rs.getString("department"),
                    rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("⚠️ Error finding student: " + e.getMessage());
        }
        return null;
    }

    // Getters
    public String getName() { return name; }
    public String getMatricNo() { return matricNo; }
    public String getDepartment() { return department; }
    public String getPassword() { return password; }
    public String getFaculty() { 
        return faculty;
    }

    int getId() {
      return id; 
    }

    String getYearsInSchool() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}

