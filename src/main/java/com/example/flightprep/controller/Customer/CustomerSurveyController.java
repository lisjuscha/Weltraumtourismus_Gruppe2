package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.TransactionService;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.RadioButoonReader;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class CustomerSurveyController extends CustomerController{

    @FXML private TextField heightTextField;
    @FXML private TextField weightTextField;
    @FXML private ComboBox<String> alcoholComboBox;
    @FXML private ComboBox<String> smokeComboBox;
    @FXML private ToggleGroup trainingGroup;
    @FXML private ToggleGroup disabilityGroup;
    @FXML private TextArea disabilityTextArea;
    @FXML private CheckBox heartDiseaseCheckBox;
    @FXML private CheckBox bloodPreasureCheckBox;
    @FXML private CheckBox heartbeatCheckBox;
    @FXML private CheckBox strokeCheckBox;
    @FXML private CheckBox asthmaCheckBox;
    @FXML private CheckBox lungDiseaseCheckBox;
    @FXML private CheckBox seizureCheckBox;
    @FXML private CheckBox neurologicalChechBox;
    @FXML private ToggleGroup cardioGroup;
    @FXML private ToggleGroup lungGroup;
    @FXML private ToggleGroup medicationGroup;
    @FXML private ToggleGroup allergieGroup;
    @FXML private ToggleGroup surgeryGroup;
    @FXML private ToggleGroup injuryGroup;


    public void initialize() {
        // Initialize the ComboBox with options
        alcoholComboBox.getItems().addAll("No",
                "Occasionally (1–2 times/month)",
                "Regularly (1–2 times/week)",
                "Frequently (3+ times/week)");
        smokeComboBox.getItems().addAll("Yes", "No", "Occasionally");

        // Set default values
        alcoholComboBox.setValue("--Select--");
        smokeComboBox.setValue("--Select--");

        // Set the default visibility of the disability text area
        disabilityTextArea.setVisible(false);

        // Add listener to disabilityGroup
        disabilityGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                boolean isYesSelected = "Yes".equals(selectedRadioButton.getText());
                disabilityTextArea.setVisible(isYesSelected);

                // Optional: Clear the text area if "No" is selected
                if (!isYesSelected) {
                    disabilityTextArea.clear();
                }
            } else {
                // No selection - hide the text area
                disabilityTextArea.setVisible(false);
            }
        });
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Textfelder prüfen
        if (heightTextField.getText().isEmpty())
            errors.append("Basic information: Height is required.\n");
        if (weightTextField.getText().isEmpty())
            errors.append("Basic information: Weight is required.\n");

        // ComboBox prüfen (Alkohol und Rauchen)
        if (alcoholComboBox.getValue() == "--Select--")
            errors.append("Basic information: Please select if you drink alcohol.\n");
        if (smokeComboBox.getValue() == "--Select--")
            errors.append("Basic information: Please select if you smoke.\n");

        // Check ToggleGroups (Training, Behinderung, etc.)
        if (trainingGroup.getSelectedToggle() == null)
            errors.append("Basic information: Please answer the training question.\n");
        if (disabilityGroup.getSelectedToggle() == null) {
            errors.append("Basic information: Please answer the disability question.\n");
        } else {
            // Get the text of the selected toggle
            String selectedText = ((RadioButton) disabilityGroup.getSelectedToggle()).getText();
            if ("Yes".equals(selectedText)) {
                // Only check the text area if "Yes" is selected
                if (disabilityTextArea.getText().isEmpty()) {
                    errors.append("Please complete basic information\n");
                }
        }
        }

        // ToggleGroups regarding medical history
        if (cardioGroup.getSelectedToggle() == null)
            errors.append("Please complete basic information.\n");
        if (lungGroup.getSelectedToggle() == null)
            errors.append("Please complete basic information.\n");

        // ToggleGroups regarding medication, allergies and general health
        if (medicationGroup.getSelectedToggle() == null)
            errors.append("Please answer the medication-related question.\n");
        if (allergieGroup.getSelectedToggle() == null)
            errors.append("Please answer the allergy-related question.\n");
        if (surgeryGroup.getSelectedToggle() == null)
            errors.append("Please answer the surgery-related question.\n");
        if (injuryGroup.getSelectedToggle() == null)
            errors.append("Please answer the injury-related question.\n");

        // Fehler anzeigen, wenn welche vorhanden sind
        if (errors.length() > 0) {
            showAlert("Validation Error", errors.toString());
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);              // Title of the alert box
        alert.setHeaderText(null);          // Remove the header (optional)
        alert.setContentText(message);     // The content of the alert (the error message)
        // Show the alert and wait for the user to close it
        alert.showAndWait();
    }

    @FXML
    private void submitSurvey() {
        // First, validate the form inputs
        if (!validateForm()) {
            return; // If validation fails, don't proceed
        }

        // Collect data from the form fields
        String height = heightTextField.getText();
        String weight = weightTextField.getText();
        String alcoholUsage = alcoholComboBox.getValue();
        String smokingStatus = smokeComboBox.getValue();

        // Get selected radio buttons from ToggleGroups
        boolean trainingStatus = RadioButoonReader.getToggleGroupBoolean(trainingGroup);
        boolean disabilityStatus = RadioButoonReader.getToggleGroupBoolean(disabilityGroup);

        String disabilityDetails = disabilityTextArea.getText();

        // Collect selected CheckBoxes (health conditions)
        boolean hasHeartDisease = heartDiseaseCheckBox.isSelected();
        boolean hasBloodPressureIssue = bloodPreasureCheckBox.isSelected();
        boolean hasHeartbeatIssues = heartbeatCheckBox.isSelected();
        boolean hasStrokeHistory = strokeCheckBox.isSelected();
        boolean hasAsthma = asthmaCheckBox.isSelected();
        boolean hasLungDisease = lungDiseaseCheckBox.isSelected();
        boolean hasSeizure = seizureCheckBox.isSelected();
        boolean hasNeurologicalConditions = neurologicalChechBox.isSelected();

        // Collect data from ToggleGroups (cardio, lung, etc.)
        boolean cardioStatus = RadioButoonReader.getToggleGroupBoolean(cardioGroup);
        boolean lungStatus = RadioButoonReader.getToggleGroupBoolean(lungGroup);

        // Collect data from ToggleGroups (medication, allergies, etc.)
        boolean takesPrescriptions = RadioButoonReader.getToggleGroupBoolean(medicationGroup);
        boolean hasAllergies = RadioButoonReader.getToggleGroupBoolean(allergieGroup);
        boolean hadSurgery = RadioButoonReader.getToggleGroupBoolean(surgeryGroup);
        boolean hadSeriousInjury = RadioButoonReader.getToggleGroupBoolean(injuryGroup);


        // Store the data in an appropriate structure or process it further (e.g., saving to a database)
        // Example: Assuming you have a Survey model to store this data

        MedicalData data = new MedicalData(
                height, weight, alcoholUsage, smokingStatus,
                trainingStatus, disabilityStatus, disabilityDetails,
                hasHeartDisease, hasBloodPressureIssue, hasHeartbeatIssues,
                hasStrokeHistory, hasAsthma, hasLungDisease, hasSeizure, hasNeurologicalConditions,
                cardioStatus, lungStatus, takesPrescriptions, hasAllergies, hadSurgery, hadSeriousInjury
        );

        // Call a service or DAO method to save the survey to the database

        try {
            TransactionService.submitMedicalSurvey(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Optionally, show a success message or reset the form
        showAlert("Success", "Survey submitted successfully!");
    }


}
