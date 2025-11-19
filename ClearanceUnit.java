import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public abstract class ClearanceUnit {
    protected String unitName;
    protected boolean isCleared;
    protected LocalDateTime clearanceTime;

    public ClearanceUnit(String unitName) {
        this.unitName = unitName;
        this.isCleared = false;
    }

    public boolean login(String username, String password) {
        return username.equalsIgnoreCase(unitName) && password.equals("1234");
    }

    public void clearStudent(Student student) {
        isCleared = true;
        clearanceTime = LocalDateTime.now();
        String formattedTime = clearanceTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println(unitName + " cleared " + student.getName() + " at " + formattedTime);
        saveClearanceToDatabase(student, formattedTime);
    }

    private void saveClearanceToDatabase(Student student, String time) {
        String sql = "INSERT INTO clearance_log (student_id, unit_name, cleared_at) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, student.getId());
            stmt.setString(2, unitName);
            stmt.setString(3, time);
            stmt.executeUpdate();

            System.out.println("🗂️ Saved clearance record in database.");

        } catch (SQLException e) {
            System.out.println("⚠️ Database error while saving clearance: " + e.getMessage());
        }
    }

    public boolean isCleared() {
        return isCleared;
    }

    public String getUnitName() {
        return unitName;
    }
}
