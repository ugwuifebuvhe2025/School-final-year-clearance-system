import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDashboard {

    private int studentId;
    private Label statusLabel;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
    }

    public void show(Stage stage) {
        stage.setTitle("Student Dashboard");

        Label title = new Label("Welcome to Student Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        refreshStatus();

        Button uploadButton = new Button("Upload Clearance Document");
        uploadButton.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;");
        uploadButton.setOnAction(e -> new StudentUploadDialog(studentId, this::refreshStatus).show());

        Button downloadBtn = new Button("Download Certificate");
        downloadBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
        downloadBtn.setOnAction(e -> {
            Student s = Student.getStudentById(studentId);
            CertificateGenerator.generateCertificate(s);
        });
        downloadBtn.setDisable(!isFullyCleared());

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            stage.close();
            try {
                new HomePage().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(20, title, statusLabel, uploadButton, downloadBtn, logoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF);");

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void refreshStatus() {
        String query = "SELECT * FROM clearance_status WHERE student_id = ?";
        StringBuilder sb = new StringBuilder("Clearance Status:\n\n");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                sb.append("Library: ").append(rs.getBoolean("library_cleared") ? "✅" : "❌").append("\n");
                sb.append("Hostel: ").append(rs.getBoolean("hostel_cleared") ? "✅" : "❌").append("\n");
                sb.append("Medical: ").append(rs.getBoolean("medical_cleared") ? "✅" : "❌").append("\n");
                sb.append("Bursary: ").append(rs.getBoolean("bursary_cleared") ? "✅" : "❌").append("\n");
                sb.append("Faculty: ").append(rs.getBoolean("faculty_cleared") ? "✅" : "❌").append("\n");
                sb.append("Department: ").append(rs.getBoolean("department_cleared") ? "✅" : "❌").append("\n");
                sb.append("Exam: ").append(rs.getBoolean("exam_cleared") ? "✅" : "❌").append("\n");
                sb.append("Security: ").append(rs.getBoolean("security_cleared") ? "✅" : "❌").append("\n");
                 sb.append("Registry: ").append(rs.getBoolean("Registry_cleared") ? "✅" : "❌").append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        statusLabel.setText(sb.toString());
    }

    private boolean isFullyCleared() {
        String query = "SELECT * FROM clearance_status WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("library_cleared") &&
                       rs.getBoolean("hostel_cleared") &&
                       rs.getBoolean("medical_cleared") &&
                       rs.getBoolean("bursary_cleared") &&
                       rs.getBoolean("faculty_cleared") &&
                       rs.getBoolean("department_cleared") &&
                       rs.getBoolean("exam_cleared") &&
                       rs.getBoolean("security_cleared");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
