import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentSignup {

    public void show(Stage stage) {
        stage.setTitle("Student Signup");

        Label title = new Label("STUDENT SIGNUP");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Signup fields
        TextField fullName = new TextField();
        fullName.setPromptText("Full Name");

        TextField matricNo = new TextField();
        matricNo.setPromptText("Matric Number");

        TextField department = new TextField();
        department.setPromptText("Department");

        TextField faculty = new TextField();
        faculty.setPromptText("Faculty");

        TextField years = new TextField();
        years.setPromptText("Number of Years in School");

        PasswordField password = new PasswordField();
        password.setPromptText("Create Password");

        Button signupBtn = new Button("Create Account");
        signupBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold;");

        Label msg = new Label();
        msg.setStyle("-fx-text-fill: white;");

        // Signup action
        signupBtn.setOnAction(e -> {
            if (fullName.getText().isEmpty() || matricNo.getText().isEmpty() ||
                department.getText().isEmpty() || faculty.getText().isEmpty() ||
                years.getText().isEmpty() || password.getText().isEmpty()) {
                msg.setText("⚠️ Please fill all fields!");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                int yearsInSchool = Integer.parseInt(years.getText());

                // Insert student info
                String sqlStudent = "INSERT INTO students (full_name, matric_number, department, faculty, years_in_school, password) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sqlStudent, PreparedStatement.RETURN_GENERATED_KEYS);
                stmt.setString(1, fullName.getText());
                stmt.setString(2, matricNo.getText());
                stmt.setString(3, department.getText());
                stmt.setString(4, faculty.getText());
                stmt.setInt(5, yearsInSchool);
                stmt.setString(6, password.getText());
                stmt.executeUpdate();

                // Get generated student ID
                int studentId = -1;
                var rs = stmt.getGeneratedKeys();
                if (rs.next()) studentId = rs.getInt(1);

                // Insert clearance_status row (all FALSE by default)
                String sqlClearance = "INSERT INTO clearance_status (student_id, department_cleared) VALUES (?, FALSE)";
                PreparedStatement stmt2 = conn.prepareStatement(sqlClearance);
                stmt2.setInt(1, studentId);
                stmt2.executeUpdate();

                msg.setText("✅ Account created successfully!");
            } catch (Exception ex) {
                msg.setText("⚠️ Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Back button action
        backBtn.setOnAction(e -> {
            try {
                new HomePage().start(new Stage());
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Layout
        VBox layout = new VBox(10, title, fullName, matricNo, department, faculty, years, password,
                signupBtn, backBtn, msg);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #1565C0, #0D47A1);");

        stage.setScene(new Scene(layout, 450, 500));
        stage.show();
    }
}
