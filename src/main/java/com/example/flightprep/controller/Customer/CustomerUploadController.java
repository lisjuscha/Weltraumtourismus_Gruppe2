package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.service.FileUploadService;
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

    @FXML
    private ListView<String> fileListView;

    private final FileUploadService fileUploadService;
    private final CustomerService customerService;
    private final List<File> tempFiles;

    public CustomerUploadController() {
        this.fileUploadService = new FileUploadService();
        this.customerService = CustomerService.getInstance();
        this.tempFiles = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        try {
            fileUploadService.createDirectories();
        } catch (IOException e) {
            showError("Directory Error", "Error creating directories: " + e.getMessage());
        }
    }

    @FXML
    public void handleFileUpload() {
        List<File> files = showFileChooser();
        if (files == null) return;

        for (File file : files) {
            if (!fileUploadService.isValidFileSize(file)) {
                showError("File too large", file.getName() + " is too large. Maximum size is 10MB.");
                continue;
            }

            try {
                File tempFile = fileUploadService.saveToTemp(file);
                tempFiles.add(tempFile);
                fileListView.getItems().add(file.getName());
            } catch (IOException e) {
                showError("Upload error", "Error saving " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        if (tempFiles.isEmpty()) {
            showError("No Files", "Please upload files before submitting.");
            return;
        }

        try {
            String userId = SessionManager.getCurrentUserId();
            fileUploadService.moveFilesToFinalDirectory(tempFiles, userId);
            customerService.updateFileUploadStatus(userId);

            cleanupAndNavigate(event);
        } catch (Exception e) {
            showError("Error", "Upload failed: " + e.getMessage());
        }
    }

    private List<File> showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Filetypes (*.pdf, *.jpg, *.png)", "*.pdf", "*.jpg", "*.png")
        );
        return fileChooser.showOpenMultipleDialog(fileListView.getScene().getWindow());
    }

    private void cleanupAndNavigate(ActionEvent event) throws IOException {
        tempFiles.clear();
        fileListView.getItems().clear();
        showSuccess("Success", "Files uploaded successfully.");
        SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", event);
    }
}

