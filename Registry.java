import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.io.IOException;

public class Registry extends ClearanceUnit {
    public Registry() {
        super("Registry");
    }
 
    // ✅ Check in DB if all required units cleared the student
    public boolean allCleared(Student student) {
        String[] requiredUnits = {
            "Library", "Hostel", "Medical", "Bursary", "Faculty", "Exam", "Security"
        };

        String sql = "SELECT unit_name FROM clearances WHERE matric_no = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, student.getId());
            ResultSet rs = stmt.executeQuery();

            java.util.Set<String> clearedUnits = new java.util.HashSet<>();
            while (rs.next()) {
                clearedUnits.add(rs.getString("unit_name"));
            }

            for (String unit : requiredUnits) {
                if (!clearedUnits.contains(unit)) {
                    System.out.println("❌ " + unit + " has not cleared the student yet!");
                    return false;
                }
            }

            System.out.println("✅ All units have cleared " + student.getName());
            generateCertificate(student);
            return true;

        } catch (SQLException e) {
            System.out.println("⚠️ Error checking clearance: " + e.getMessage());
            return false;
        }
    }

    // ✅ Generate and save certificate if all cleared
    public void generateCertificate(Student student) {
        String certificateText = "=============================================\n" +
                                 "        FINAL YEAR CLEARANCE CERTIFICATE\n" +
                                 "=============================================\n" +
                                 "Student Name : " + student.getName() + "\n" +
                                 "Matric Number: " + student.getMatricNo() + "\n" +
                                 "Department   : " + student.getDepartment() + "\n" +
                                 "Status       : CLEARED\n" +
                                 "Cleared Date : " + LocalDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                                 "---------------------------------------------\n" +
                                 "Congratulations! You have been fully cleared.\n" +
                                 "=============================================\n";

        System.out.println("\n✅ FINAL CLEARANCE CERTIFICATE GENERATED!");
        System.out.println(certificateText);

        saveCertificateToFile(student, certificateText);
    }

    private void saveCertificateToFile(Student student, String content) {
        String fileName = student.getName().replace(" ", "_") + "_ClearanceCertificate.txt";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
            System.out.println("📄 Certificate saved as: " + fileName);
        } catch (IOException e) {
            System.out.println("⚠️ Error saving certificate: " + e.getMessage());
        }
    }
}
