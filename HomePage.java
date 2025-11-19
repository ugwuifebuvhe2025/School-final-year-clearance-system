import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomePage extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("University Final Year Clearance System");

        // Title
        Text title = new Text("Final Year Clearance System");
        title.setFont(Font.font("Arial", 26));
        title.setFill(Color.WHITE);

        // Buttons
        Button adminLoginBtn = new Button("Admin Login");
        Button studentLoginBtn = new Button("Student Login");
        Button studentSignupBtn = new Button("Student Signup");

        // Style buttons
        String buttonStyle = "-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;";
        adminLoginBtn.setStyle(buttonStyle);
        studentLoginBtn.setStyle(buttonStyle);
        studentSignupBtn.setStyle(buttonStyle);

        // Button actions
        adminLoginBtn.setOnAction(e -> {
            try {
                new AdminLogin().show(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        studentLoginBtn.setOnAction(e -> {
            try {
                new StudentLogin().show(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        studentSignupBtn.setOnAction(e -> {
            try {
                new StudentSignup().show(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Layout
        VBox layout = new VBox(20, title, adminLoginBtn, studentLoginBtn, studentSignupBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF); -fx-padding: 40;");

        // Scene and show stage
        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

