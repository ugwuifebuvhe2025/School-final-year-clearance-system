

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UploadDocument {

    public static void showUploadWindow(Stage parentStage, int studentId, String section) {
        Stage stage = new Stage();
        stage.setTitle("Upload " + section + " Clearance Document");

        Label title = new Label("Upload your " + section + " document (PDF only)");
        title.setStyle("-fx-font-size: 16; -fx-text-fill: #0D47A1; -fx-font-weight: bold;");

        Button selectBtn = new Button("Choose PDF");
        Label fileLabel = new Label("No file selected.");
        Button uploadBtn = new Button("Upload");
        uploadBtn.setDisable(true);

        final File[] selectedFile = new File[1];

        selectBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText(file.getName());
                uploadBtn.setDisable(false);
            }
        });

        uploadBtn.setOnAction(e -> {
            if (selectedFile[0] != null) {
                uploadPDF(studentId, section, selectedFile[0]);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upload Successful");
                alert.setContentText("✅ Document uploaded successfully!");
                alert.showAndWait();
                stage.close();
            }
        });

        VBox layout = new VBox(15, title, selectBtn, fileLabel, uploadBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        stage.setScene(new Scene(layout, 400, 250));
        stage.initOwner(parentStage);
        stage.show();
    }

    private void uploadDocument(String section, File file) {
        String sql = "INSERT INTO uploaded_documents (student_id, section, file_name, file_data, status, uploaded_on) "
                + "VALUES (?, ?, ?, ?, 'Pending', NOW())";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); FileInputStream fis = new FileInputStream(file)) {
            int studentId = 0;

            stmt.setInt(1, studentId);
            stmt.setString(2, section);
            stmt.setString(3, file.getName());
            stmt.setBinaryStream(4, fis, (int) file.length());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void uploadPDF(int studentId, String section, File file) {
        try (Connection conn = DatabaseConnection.getConnection(); FileInputStream fis = new FileInputStream(file)) {

            String sql = "INSERT INTO uploaded_documents (student_id, section, file_name, file_data) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setString(2, section);
            stmt.setString(3, file.getName());
            stmt.setBinaryStream(4, fis, (int) file.length());
            stmt.executeUpdate();

        } catch (Exception ex) {
        }
    }
}
