package com.example.flightprep.controller.Doctor;

import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.model.User;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.service.UserService;
import com.example.flightprep.util.DbConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Desktop;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.sql.SQLException;
import java.util.Optional;




public class DocPatientSummaryController extends DocController {
    private String currentPatientId;

    @FXML
    private ListView<String> documentsListView;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label smokingLabel;
    @FXML
    private Label alcoholLabel;
    @FXML
    private Label trainingLabel;
    @FXML
    private Label disabilityLabel;
    @FXML
    private Label disabilityDetailsLabel;

    @FXML
    private Label heartDiseaseLabel;
    @FXML
    private Label bloodPressureLabel;
    @FXML
    private Label heartbeatLabel;
    @FXML
    private Label strokeLabel;
    @FXML
    private Label asthmaLabel;
    @FXML
    private Label lungDiseaseLabel;
    @FXML
    private Label seizureLabel;
    @FXML
    private Label neurologicalLabel;
    @FXML
    private Label hspRespiratoryCardioLabel;
    @FXML
    private Label hspHeartLungLabel;

    @FXML
    private Label medicationLabel;
    @FXML
    private Label allergiesLabel;
    @FXML
    private Label surgeryLabel;
    @FXML
    private Label injuryLabel;

    @FXML
    public void initialize() {
        loadDocuments();
        setupDocumentOpening();
    }

    public void loadPatientData(String patientId) {
        this.currentPatientId = patientId;
        try (Connection conn = DbConnection.getConnection()) {
            MedicalDataDAO medicalDataDAO = new MedicalDataDAO(conn);
            MedicalData data = medicalDataDAO.getDataByUserId(patientId);

            if (data != null) {
                // Grundlegende Informationen
                heightLabel.setText(data.getHeight() + " cm");
                weightLabel.setText(data.getWeight() + " kg");
                smokingLabel.setText(data.getSmokingStatus());
                alcoholLabel.setText(data.getAlcoholConsumption());
                trainingLabel.setText(data.isTrainingStatus() ? "Yes" : "No");
                disabilityLabel.setText(data.getDisabilityStatus() ? "Yes" : "No");
                if (data.getDisabilityStatus()) {
                    disabilityDetailsLabel.setText(data.getDisabilityDetails());
                    disabilityDetailsLabel.setVisible(true);
                    disabilityDetailsLabel.setDisable(false);
                }

                // Medizinische Vorgeschichte
                heartDiseaseLabel.setText(data.isHeartDisease() ? "Yes" : "No");
                bloodPressureLabel.setText(data.isHighBloodPressure() ? "Yes" : "No");
                heartbeatLabel.setText(data.isIrregularHeartbeat() ? "Yes" : "No");
                strokeLabel.setText(data.isStrokeHistory() ? "Yes" : "No");
                asthmaLabel.setText(data.isAsthma() ? "Yes" : "No");
                lungDiseaseLabel.setText(data.isLungDisease() ? "Yes" : "No");
                seizureLabel.setText(data.isSeizureHistory() ? "Yes" : "No");
                neurologicalLabel.setText(data.isNeurologicalDisorder() ? "Yes" : "No");
                hspRespiratoryCardioLabel.setText(data.isHsp_respiratory_cardio() ? "Yes" : "No");
                hspHeartLungLabel.setText(data.isHsp_heart_lung() ? "Yes" : "No");

                // Zusätzliche Informationen
                medicationLabel.setText(data.isPersc_med() ? "Yes" : "No");
                allergiesLabel.setText(data.isAllergies() ? "Yes" : "No");
                surgeryLabel.setText(data.isSurgery() ? "Yes" : "No");
                injuryLabel.setText(data.isSer_injury() ? "Yes" : "No");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDocuments() {
        try {
            File uploadsDir = new File("Data/uploads");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs();
            }

            if (uploadsDir.isDirectory()) {
                File[] files = uploadsDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return !file.isDirectory();
                    }
                });

                if (files != null) {
                    List<String> fileNames = new ArrayList<>();
                    for (File file : files) {
                        fileNames.add(file.getName());
                    }
                    Collections.sort(fileNames);
                    documentsListView.setItems(FXCollections.observableArrayList(fileNames));
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Dokumente: " + e.getMessage());
        }
    }

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

    private void openFile(File file) {
        if (!file.exists()) {
            System.err.println("File does not exist: " + file.getPath());
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

    @FXML
    private void handleDeclareButton() {
        Dialog<Pair<Boolean, String>> dialog = new Dialog<>();
        dialog.setTitle("Flight clearance");
        dialog.setHeaderText("Please confirm the flight clearance for the patient.");

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Dialog-Inhalt erstellen
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

        Optional<Pair<Boolean, String>> result = dialog.showAndWait();

        result.ifPresent(decision -> {
            try {
                CustomerService customerService = CustomerService.getInstance();
                customerService.saveDeclaration(currentPatientId, decision.getKey(), decision.getValue());
                showSuccess("Success", "Your Choice has been saved successfully.");
            } catch (SQLException e) {
                showError("Error", "Error while saving:" + e.getMessage());
            }
        });
    }
}