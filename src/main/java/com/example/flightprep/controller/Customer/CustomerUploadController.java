package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.service.FileUploadService;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `CustomerUploadController` class manages the file upload view for customers in the application.
 * It allows customers to upload required files, validate their size, and submit them for flight preparation.
 * This class extends `CustomerController`.
 */
public class CustomerUploadController extends CustomerController {

    @FXML
    private ListView<String> fileListView;

    private final FileUploadService fileUploadService;
    private final CustomerService customerService;
    private final List<File> tempFiles;

    /**
     * Constructs a new `CustomerUploadController` and initializes the required services and temporary file list.
     */
    public CustomerUploadController() {
        this.fileUploadService = new FileUploadService();
        this.customerService = CustomerService.getInstance();
        this.tempFiles = new ArrayList<>();
    }

    /**
     * Initializes the file upload view by creating necessary directories.
     * Displays an error message if directory creation fails.
     */
    @FXML
    public void initialize() {
        try {
            fileUploadService.createDirectories();
        } catch (IOException e) {
            showError("Directory Error", "Error creating directories: " + e.getMessage());
        }
    }

    /**
     * Handles the file upload process by opening a file chooser dialog.
     * Validates the file size and saves valid files to a temporary directory.
     * Displays an error message for invalid files.
     */
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

    /**
     * Handles the submission of uploaded files.
     * Moves files from the temporary directory to the final directory and updates the upload status.
     * Displays an error message if the submission fails.
     *
     * @param event The `ActionEvent` triggered by the submit button click.
     */
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

    /**
     * Opens a file chooser dialog to allow the user to select files for upload.
     *
     * @return A list of selected files, or `null` if no files were selected.
     */
    private List<File> showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Filetypes (*.pdf, *.jpg, *.png)", "*.pdf", "*.jpg", "*.png")
        );
        return fileChooser.showOpenMultipleDialog(fileListView.getScene().getWindow());
    }

    /**
     * Cleans up temporary files and navigates to the next scene after successful submission.
     *
     * @param event The `ActionEvent` triggered by the submit button click.
     * @throws IOException If an error occurs during scene switching.
     */
    private void cleanupAndNavigate(ActionEvent event) throws IOException {
        tempFiles.clear();
        fileListView.getItems().clear();
        showSuccess("Success", "Files uploaded successfully.");
        SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", event);
    }
}

