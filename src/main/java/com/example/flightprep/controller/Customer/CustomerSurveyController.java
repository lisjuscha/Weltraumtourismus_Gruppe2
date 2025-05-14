package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.RadioButtonReader;
import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * The `CustomerSurveyController` class manages the medical survey view for customers in the application.
 * It allows customers to input their medical data, validate the form, and submit the survey.
 * This class extends `CustomerController`.
 */
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

    /**
     * Constructs a new `CustomerSurveyController` and initializes the `CustomerService` instance.
     */
    public CustomerSurveyController() {
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Initializes the survey view by setting up combo boxes, toggle groups, and event listeners.
     * This method is called automatically after the FXML file has been loaded.
     */
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

    /**
     * Handles the submission of the survey form.
     * Validates the form, collects the data, and submits it to the `CustomerService`.
     * If successful, switches to the preparation screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    private void submitSurvey(ActionEvent actionEvent) {
        if (!validateForm()) {
            return;
        }

        MedicalData data = collectFormData();

        try {
            customerService.submitMedicalData(data);
            showSuccess("Success", "Survey submitted successfully!");
            try {
                SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", actionEvent);
            } catch (IOException e) {
                showError("Error", "Failed to switch to preparation screen: " + e.getMessage());
            }
        } catch (SQLException e) {
            showError("Error", "Failed to submit survey: " + e.getMessage());
        }
    }

    /**
     * Collects the data from the survey form and creates a `MedicalData` object.
     *
     * @return A `MedicalData` object containing the collected form data.
     */
    private MedicalData collectFormData() {
        return new MedicalData(
                heightTextField.getText(),
                weightTextField.getText(),
                alcoholComboBox.getValue(),
                smokeComboBox.getValue(),
                RadioButtonReader.getToggleGroupBoolean(trainingGroup),
                RadioButtonReader.getToggleGroupBoolean(disabilityGroup),
                disabilityTextArea.getText(),
                heartDiseaseCheckBox.isSelected(),
                bloodPreasureCheckBox.isSelected(),
                heartbeatCheckBox.isSelected(),
                strokeCheckBox.isSelected(),
                asthmaCheckBox.isSelected(),
                lungDiseaseCheckBox.isSelected(),
                seizureCheckBox.isSelected(),
                neurologicalChechBox.isSelected(),
                RadioButtonReader.getToggleGroupBoolean(cardioGroup),
                RadioButtonReader.getToggleGroupBoolean(lungGroup),
                RadioButtonReader.getToggleGroupBoolean(medicationGroup),
                RadioButtonReader.getToggleGroupBoolean(allergieGroup),
                RadioButtonReader.getToggleGroupBoolean(surgeryGroup),
                RadioButtonReader.getToggleGroupBoolean(injuryGroup)
        );
    }

    /**
     * Validates the survey form to ensure all required fields are filled and properly formatted.
     * Displays an error message if validation fails.
     *
     * @return `true` if the form is valid, `false` otherwise.
     */
    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Height validation
        if (heightTextField.getText().isEmpty()) {
            errors.append("Basic information: Height is required.\n");
        } else {
            try {
                double height = Double.parseDouble(heightTextField.getText().replace(",", "."));
            } catch (NumberFormatException e) {
                errors.append("Invalid height format.\nPlease enter a number with optional decimal places (e.g. 175.5)\n");
            }
        }
        // Weight validation
        if (weightTextField.getText().isEmpty()) {
            errors.append("Basic information: Weight is required.\n");
        } else {
            try {
                double weight = Double.parseDouble(weightTextField.getText().replace(",", "."));
            } catch (NumberFormatException e) {
                errors.append("Invalid weight format.\nPlease enter a number with optional decimal places (e.g. 75.5)\n");
            }
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