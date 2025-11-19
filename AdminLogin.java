import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminLogin {

    // Rename start(Stage) → show(Stage)
    public void show(Stage stage) {
        stage.setTitle("Admin Login");

        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: white; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;");

        Label message = new Label("");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (authenticateAdmin(username, password)) {
                stage.close();
                try {
                    new AdminDashboard().show(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                message.setText("Invalid username or password!");
            }
        });

        VBox layout = new VBox(15, title, usernameField, passwordField, loginBtn, message);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF);");

        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private boolean authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
