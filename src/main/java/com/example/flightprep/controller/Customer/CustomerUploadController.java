package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerUploadController extends CustomerController {
    private static final String UPLOAD_DIR = "data/uploads";
    private static final String TEMP_DIR = "data/temp";
    private List<File> tempFiles = new ArrayList<>();
    private final DatabaseConnection databaseConnection;

    @FXML
    private ListView<String> fileListView;

    public CustomerUploadController() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    @FXML
    public void initialize() {
        createDirectories();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Files.createDirectories(Paths.get(TEMP_DIR));
        } catch (IOException e) {
            showError("Directory Error", "Error while creating directory: " + e.getMessage());
        }
    }

    @FXML
    public void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Filetypes (*.pdf, *.jpg, *.png)", "*.pdf", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(fileListView.getScene().getWindow());

        if (files != null) {
            for (File file : files) {
                if (file.length() <= 10 * 1024 * 1024) {
                    try {
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        String fileName = timestamp + "_" + file.getName();
                        Path tempPath = Paths.get(TEMP_DIR, fileName);

                        Files.copy(file.toPath(), tempPath, StandardCopyOption.REPLACE_EXISTING);
                        tempFiles.add(tempPath.toFile());
                        fileListView.getItems().add(file.getName());

                    } catch (IOException e) {
                        showError("Upload error",
                                "Error saving: " + file.getName() + ": " + e.getMessage());
                    }
                } else {
                    showError("File too large",
                            file.getName() + " is too large. Maximum size is 10MB.");
                }
            }
        }
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        if (tempFiles.isEmpty()) {
            showError("No Files", "Please upload files before submitting.");
            return;
        }
        Connection conn = null;
        try {
            // Move files to final directory
            for (File tempFile : tempFiles) {
                String fileName = SessionManager.getCurrentUserId() + "_" + tempFile.getName();
                Path targetPath = Paths.get(UPLOAD_DIR, fileName);
                Files.move(tempFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Update database
            conn = databaseConnection.getConnection();
            UserDAO userDAO = new UserDAO(conn);
            userDAO.updateFileUploadStatus();
            conn.commit();


            tempFiles.clear();
            fileListView.getItems().clear();
            showSuccess("Successful upload", "Files have been successfully uploaded.");

            // Switch to the next scene
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml",
                    event);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    showError("Database Error", "Rollback failed: " + rollbackEx.getMessage());
                }
            }
            showError("Database Error", "Error updating database: " + e.getMessage());
        } catch (IOException e) {
            showError("File Error", "Error moving files: " + e.getMessage());
        } finally {
            if (conn != null) {
                databaseConnection.closeConnection(conn);
            }
        }
    }
}

