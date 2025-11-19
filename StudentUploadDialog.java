

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentUploadDialog {

    private final int studentId;
    private final Runnable refreshCallback;

    public StudentUploadDialog(int studentId, Runnable refreshCallback) {
        this.studentId = studentId;
        this.refreshCallback = refreshCallback;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Upload Clearance Document");

        ComboBox<String> sectionCombo = new ComboBox<>();
        sectionCombo.getItems().addAll("Library", "Hostel", "Medical", "Bursary", "Faculty", "Department", "Exam", "Security");

        Button chooseFileBtn = new Button("Choose PDF File");
        chooseFileBtn.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;");
        Label fileLabel = new Label("No file selected");

        final File[] selectedFile = new File[1];
        chooseFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            selectedFile[0] = fileChooser.showOpenDialog(stage);
            if (selectedFile[0] != null) fileLabel.setText(selectedFile[0].getName());
        });

        Button uploadBtn = new Button("Upload");
        uploadBtn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        uploadBtn.setOnAction(e -> {
            if (sectionCombo.getValue() == null || selectedFile[0] == null) {
                new Alert(Alert.AlertType.ERROR, "Please select a section and file!").show();
                return;
            }

            uploadDocument(sectionCombo.getValue(), selectedFile[0]);
            new Alert(Alert.AlertType.INFORMATION, "File uploaded successfully!").show();
            refreshCallback.run();
            stage.close();
        });

        VBox layout = new VBox(15, sectionCombo, chooseFileBtn, fileLabel, uploadBtn);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF);");

        stage.setScene(new Scene(layout, 400, 250));
        stage.show();
    }

    private void uploadDocument(String section, File file) {
        String sql = "INSERT INTO uploaded_documents (student_id, section, file_name, file_data, uploaded_on) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(file)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, section);
            stmt.setString(3, file.getName());
            stmt.setBinaryStream(4, fis, (int) file.length());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
