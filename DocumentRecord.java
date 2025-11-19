

import javafx.beans.property.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DocumentRecord {
    private final int id;
    private final int studentId;
    private StringProperty studentName;
    private StringProperty matricNumber;
    private StringProperty section;
    private StringProperty fileName;
    private StringProperty status;
    private InputStream fileData;

    private Button openButton;
    private Button approveButton;
    private Button rejectButton;

    public DocumentRecord(int id, int studentId, String studentName, String matricNumber, String section,
                          String fileName, InputStream fileData, String status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = new SimpleStringProperty(studentName);
        this.matricNumber = new SimpleStringProperty(matricNumber);
        this.section = new SimpleStringProperty(section);
        this.fileName = new SimpleStringProperty(fileName);
        this.fileData = fileData;
        this.status = new SimpleStringProperty(status);

        openButton = new Button("Open");
        openButton.setStyle("-fx-background-color: white; -fx-text-fill: blue;");
        openButton.setOnAction(e -> openDocument());

        approveButton = new Button("Approve");
        approveButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        approveButton.setOnAction(e -> updateStatus("Approved"));

        rejectButton = new Button("Reject");
        rejectButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        rejectButton.setOnAction(e -> updateStatus("Rejected"));
    }

    private void updateStatus(String newStatus) {
        String updateSQL = "UPDATE uploaded_documents SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();

            // Update clearance status if approved
            if (newStatus.equals("Approved")) {
                String col = section.get().toLowerCase() + "_cleared";
                String sql2 = "UPDATE clearance_status SET " + col + " = TRUE WHERE student_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                    ps.setInt(1, studentId);
                    ps.executeUpdate();
                }
            }

            status.set(newStatus);
            new Alert(Alert.AlertType.INFORMATION, "Document marked as " + newStatus).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDocument() {
        try {
            String path = System.getProperty("user.home") + "/Downloads/" + fileName.get();
            FileOutputStream fos = new FileOutputStream(path);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileData.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();

            new Alert(Alert.AlertType.INFORMATION, "File saved to: " + path).show();
            java.awt.Desktop.getDesktop().open(new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Properties for TableView
    public StringProperty studentNameProperty() { return studentName; }
    public StringProperty matricNumberProperty() { return matricNumber; }
    public StringProperty sectionProperty() { return section; }
    public StringProperty fileNameProperty() { return fileName; }
    public StringProperty statusProperty() { return status; }

    public ObjectProperty<Button> openButtonProperty() { return new SimpleObjectProperty<>(openButton); }

    public ObjectProperty<HBox> actionButtonsProperty() {
        HBox hbox = new HBox(10, approveButton, rejectButton);
        return new SimpleObjectProperty<>(hbox);
    }
}
