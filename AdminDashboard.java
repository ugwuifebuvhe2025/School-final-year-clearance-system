import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboard {

    private TableView<DocumentRecord> tableView;

    public void show(Stage stage) {
        stage.setTitle("Admin Dashboard - Document Approval");

        Label title = new Label("📁 Admin Dashboard - Document Review");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        tableView = new TableView<>();
        tableView.setPlaceholder(new Label("No uploaded documents found."));

        TableColumn<DocumentRecord, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(data -> data.getValue().studentNameProperty());

        TableColumn<DocumentRecord, String> matricCol = new TableColumn<>("Matric No.");
        matricCol.setCellValueFactory(data -> data.getValue().matricNumberProperty());

        TableColumn<DocumentRecord, String> sectionCol = new TableColumn<>("Section");
        sectionCol.setCellValueFactory(data -> data.getValue().sectionProperty());

        TableColumn<DocumentRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        TableColumn<DocumentRecord, Button> openCol = new TableColumn<>("Open");
        openCol.setCellValueFactory(data -> data.getValue().openButtonProperty());

        TableColumn<DocumentRecord, HBox> actionCol = new TableColumn<>("Actions");
        actionCol.setCellValueFactory(data -> data.getValue().actionButtonsProperty());

        tableView.getColumns().addAll(studentCol, matricCol, sectionCol, statusCol, openCol, actionCol);

        // Buttons
        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle("-fx-background-color: white; -fx-text-fill: blue; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> loadDocuments());

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            stage.close();
            Platform.runLater(() -> {
                try {
                    new HomePage().start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        HBox topBar = new HBox(10, refreshBtn, logoutBtn);
        topBar.setPadding(new Insets(10));

        VBox layout = new VBox(15, title, tableView, topBar);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #007BFF, #FFFFFF);");

        Scene scene = new Scene(layout, 900, 600);
        stage.setScene(scene);
        stage.show();

        loadDocuments();
    }

    private void loadDocuments() {
        tableView.getItems().clear();

        String query = """
            SELECT u.id, s.full_name AS name, s.matric_number, u.section, u.file_name, u.file_data, u.status, s.id AS student_id
            FROM uploaded_documents u
            JOIN students s ON s.id = u.student_id
            ORDER BY u.uploaded_on DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DocumentRecord record = new DocumentRecord(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("matric_number"),
                        rs.getString("section"),
                        rs.getString("file_name"),
                        rs.getBinaryStream("file_data"),
                        rs.getString("status")
                );
                tableView.getItems().add(record);
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error loading documents: " + e.getMessage()).show();
        }
    }
}
