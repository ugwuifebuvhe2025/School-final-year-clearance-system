

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

public class CertificateGenerator {

    public static void generateCertificate(Student student) {
        String fileName = student.getMatricNo() + "_clearance_certificate.pdf";

        try {
            // 1️⃣ Generate PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.BLACK);

            document.add(new Paragraph("UNIVERSITY FINAL YEAR CLEARANCE CERTIFICATE", titleFont));
            document.add(new Paragraph("\n\nThis is to certify that:", normalFont));
            document.add(new Paragraph("Student Name: " + student.getName(), normalFont));
            document.add(new Paragraph("Matric Number: " + student.getMatricNo(), normalFont));
            document.add(new Paragraph("Faculty: " + student.getFaculty(), normalFont));
            document.add(new Paragraph("Department: " + student.getDepartment(), normalFont));
            document.add(new Paragraph("Years in School: " + student.getYearsInSchool(), normalFont));
            document.add(new Paragraph("\nHas been duly cleared by all university departments.", normalFont));
            document.add(new Paragraph("Date of Clearance: " + LocalDateTime.now(), normalFont));
            document.add(new Paragraph("\n\nCongratulations!", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLDITALIC, BaseColor.DARK_GRAY)));

            document.close();

            // 2️⃣ Upload PDF to Database
            uploadCertificateToDatabase(student.getId(), fileName);

            // 3️⃣ Notify User
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Certificate generated and uploaded successfully!\nSaved as: " + fileName);
            alert.setHeaderText("Certificate Created");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error generating certificate: " + e.getMessage());
            alert.show();
        }
    }

    private static void uploadCertificateToDatabase(int studentId, String fileName) {
        String insertSQL = "INSERT INTO uploaded_documents (student_id, section, file_name, file_data, uploaded_on) VALUES (?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL);
             FileInputStream fis = new FileInputStream(fileName)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, "Final Clearance Certificate");
            stmt.setString(3, fileName);
            stmt.setBinaryStream(4, fis, (int) new File(fileName).length());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
