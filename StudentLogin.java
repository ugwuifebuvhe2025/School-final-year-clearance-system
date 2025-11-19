import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentLogin {

    // Rename start(Stage) → show(Stage)
    public void show(Stage stage) {
        stage.setTitle("Student Login");

        Label title = new Label("Student Login");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField matricField = new TextField();
        matricField.setPromptText("Matric Number");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;");

        Label message = new Label("");
        message.setStyle("-fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            String matric = matricField.getText().trim();
            String password = passwordField.getText().trim();

            int studentId = authenticateStudent(matric, password);
            if (studentId != -1) {
                stage.close();
                try {
                    new StudentDashboard(studentId).show(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                message.setText("Invalid Matric Number or Password!");
            }
        });

        VBox layout = new VBox(15, title, matricField, passwordField, loginBtn, message);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF);");

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private int authenticateStudent(String matric, String password) {
        String query = "SELECT id FROM students WHERE matric_number = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matric);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
