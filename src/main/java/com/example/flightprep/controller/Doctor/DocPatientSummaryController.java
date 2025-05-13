package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.PatientDataDisplayController;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * The `DocPatientSummaryController` class manages the summary view for a specific patient in the application.
 * It allows doctors to view medical data, manage patient documents, and declare flight clearance for the patient.
 * This class extends `PatientDataDisplayController`.
 */
public class DocPatientSummaryController extends PatientDataDisplayController {
    private String currentPatientId;
    private final CustomerService customerService;
    @FXML private ListView<String> documentsListView;

    /**
     * Constructs a new `DocPatientSummaryController` and initializes the `CustomerService` instance.
     */
    public DocPatientSummaryController() {
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Initializes the patient summary view by loading documents and setting up event handlers.
     * This method is called automatically after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        loadDocuments();
        setupDocumentOpening();
    }

    /**
     * Loads the medical data for the specified patient and updates the UI.
     *
     * @param patientId The ID of the patient whose data is to be loaded.
     */
    public void loadPatientData(String patientId) {
        this.currentPatientId = patientId;
        try {
            MedicalData data = customerService.getMedicalData(patientId);
            updateUI(data);
        } catch (SQLException e) {
            showError("Error", "Fehler beim Laden der Patientendaten: " + e.getMessage());
        }
    }

    /**
     * Loads the list of documents associated with the patient and populates the `documentsListView`.
     */
    private void loadDocuments() {
        List<String> documents = customerService.getPatientDocuments();
        documentsListView.setItems(FXCollections.observableArrayList(documents));
    }

    /**
     * Sets up the event handler for opening documents when double-clicked in the `documentsListView`.
     */
    private void setupDocumentOpening() {
        documentsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedFileName = documentsListView.getSelectionModel().getSelectedItem();
                if (selectedFileName != null) {
                    openFile(new File("Data/uploads/" + selectedFileName));
                }
            }
        });
    }

    /**
     * Opens the specified file using the default desktop application.
     *
     * @param file The file to be opened.
     */
    private void openFile(File file) {
        if (!file.exists()) {
            showError("Error", "File does not exist: " + file.getPath());
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                showError("Error", "Desktop action not supported");
            }
        } catch (Exception e) {
            showError("Error", "Error opening file: " + e.getMessage());
        }
    }

    /**
     * Handles the action of declaring flight clearance for the patient.
     * Opens a dialog for the doctor to confirm the clearance and provide comments.
     */
    @FXML
    private void handleDeclareButton() {
        Dialog<Pair<Boolean, String>> dialog = createDeclarationDialog();
        Optional<Pair<Boolean, String>> result = dialog.showAndWait();

        result.ifPresent(decision -> {
            try {
                customerService.saveDeclaration(currentPatientId, decision.getKey(), decision.getValue());
                showSuccess("Success", "Your Choice has been saved successfully.");
            } catch (SQLException e) {
                showError("Error", "Error while saving: " + e.getMessage());
            }
        });
    }

    /**
     * Creates a dialog for flight clearance declaration, allowing the doctor to confirm or deny clearance
     * and provide additional comments.
     *
     * @return A `Dialog` object configured for flight clearance declaration.
     */
    private Dialog<Pair<Boolean, String>> createDeclarationDialog() {
        Dialog<Pair<Boolean, String>> dialog = new Dialog<>();
        dialog.setTitle("Flight clearance");
        dialog.setHeaderText("Please confirm the flight clearance for the patient.");

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ToggleGroup group = new ToggleGroup();
        RadioButton yesButton = new RadioButton("Yes");
        RadioButton noButton = new RadioButton("No");
        yesButton.setToggleGroup(group);
        noButton.setToggleGroup(group);
        yesButton.setSelected(true);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Enter your comment here...");
        commentArea.setPrefRowCount(3);

        grid.add(new Label("Flight clearence:"), 0, 0);
        grid.add(yesButton, 1, 0);
        grid.add(noButton, 2, 0);
        grid.add(new Label("Comment:"), 0, 1);
        grid.add(commentArea, 1, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new Pair<>(yesButton.isSelected(), commentArea.getText());
            }
            return null;
        });

        return dialog;
    }
}