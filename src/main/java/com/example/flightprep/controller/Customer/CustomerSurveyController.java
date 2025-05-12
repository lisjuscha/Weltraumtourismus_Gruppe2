package com.example.flightprep.controller.Customer;

import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.RadioButoonReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class CustomerSurveyController extends CustomerController {
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

    private final CustomerService customerService;

    public CustomerSurveyController() {
        this.customerService = CustomerService.getInstance();
    }

    @FXML
    public void initialize() {
        alcoholComboBox.getItems().addAll(
                "No",
                "Occasionally (1–2 times/month)",
                "Regularly (1–2 times/week)",
                "Frequently (3+ times/week)"
        );
        smokeComboBox.getItems().addAll("Yes", "No", "Occasionally");

        alcoholComboBox.setValue("--Select--");
        smokeComboBox.setValue("--Select--");

        disabilityTextArea.setVisible(false);

        disabilityGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selectedRadioButton = (RadioButton) newValue;
                boolean isYesSelected = "Yes".equals(selectedRadioButton.getText());
                disabilityTextArea.setVisible(isYesSelected);
                if (!isYesSelected) {
                    disabilityTextArea.clear();
                }
            } else {
                disabilityTextArea.setVisible(false);
            }
        });
    }

    @FXML
    private void submitSurvey(ActionEvent actionEvent) {
        if (!validateForm()) {
            return;
        }

        MedicalData data = collectFormData();

        try {
            customerService.submitMedicalData(data);
            showSuccess("Success", "Survey submitted successfully!");
        } catch (SQLException e) {
            showError("Error", "Failed to submit survey: " + e.getMessage());
        }
    }

    private MedicalData collectFormData() {
        return new MedicalData(
                heightTextField.getText(),
                weightTextField.getText(),
                alcoholComboBox.getValue(),
                smokeComboBox.getValue(),
                RadioButoonReader.getToggleGroupBoolean(trainingGroup),
                RadioButoonReader.getToggleGroupBoolean(disabilityGroup),
                disabilityTextArea.getText(),
                heartDiseaseCheckBox.isSelected(),
                bloodPreasureCheckBox.isSelected(),
                heartbeatCheckBox.isSelected(),
                strokeCheckBox.isSelected(),
                asthmaCheckBox.isSelected(),
                lungDiseaseCheckBox.isSelected(),
                seizureCheckBox.isSelected(),
                neurologicalChechBox.isSelected(),
                RadioButoonReader.getToggleGroupBoolean(cardioGroup),
                RadioButoonReader.getToggleGroupBoolean(lungGroup),
                RadioButoonReader.getToggleGroupBoolean(medicationGroup),
                RadioButoonReader.getToggleGroupBoolean(allergieGroup),
                RadioButoonReader.getToggleGroupBoolean(surgeryGroup),
                RadioButoonReader.getToggleGroupBoolean(injuryGroup)
        );
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (heightTextField.getText().isEmpty()) {
            errors.append("Basic information: Height is required.\n");
        }
        if (weightTextField.getText().isEmpty()) {
            errors.append("Basic information: Weight is required.\n");
        }
        if (alcoholComboBox.getValue() == "--Select--") {
            errors.append("Basic information: Please select if you drink alcohol.\n");
        }
        if (smokeComboBox.getValue() == "--Select--") {
            errors.append("Basic information: Please select if you smoke.\n");
        }
        if (trainingGroup.getSelectedToggle() == null) {
            errors.append("Basic information: Please answer the training question.\n");
        }
        if (disabilityGroup.getSelectedToggle() == null) {
            errors.append("Basic information: Please answer the disability question.\n");
        } else if ("Yes".equals(((RadioButton) disabilityGroup.getSelectedToggle()).getText())
                && disabilityTextArea.getText().isEmpty()) {
            errors.append("Please complete basic information\n");
        }
        if (cardioGroup.getSelectedToggle() == null) {
            errors.append("Please complete basic information.\n");
        }
        if (lungGroup.getSelectedToggle() == null) {
            errors.append("Please complete basic information.\n");
        }
        if (medicationGroup.getSelectedToggle() == null) {
            errors.append("Please answer the medication-related question.\n");
        }
        if (allergieGroup.getSelectedToggle() == null) {
            errors.append("Please answer the allergy-related question.\n");
        }
        if (surgeryGroup.getSelectedToggle() == null) {
            errors.append("Please answer the surgery-related question.\n");
        }
        if (injuryGroup.getSelectedToggle() == null) {
            errors.append("Please answer the injury-related question.\n");
        }

        if (errors.length() > 0) {
            Alert alert = createAlert("Validation Error", null, errors.toString(), Alert.AlertType.ERROR);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}