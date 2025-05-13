package com.example.flightprep.controller.BasicController;


import com.example.flightprep.model.MedicalData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * The `PatientDataDisplayController` class serves as a base controller for displaying patient medical data
 * in doctor-related views. It provides functionality to update the UI with medical data and handle
 * patient-related information.
 * This class extends `DocController`.
 */
public abstract class PatientDataDisplayController extends DocController {
    @FXML
    protected Label heightLabel;
    @FXML protected Label weightLabel;
    @FXML protected Label smokingLabel;
    @FXML protected Label alcoholLabel;
    @FXML protected Label trainingLabel;
    @FXML protected Label disabilityLabel;
    @FXML protected Label disabilityDetailsLabel;
    @FXML protected Label heartDiseaseLabel;
    @FXML protected Label bloodPressureLabel;
    @FXML protected Label heartbeatLabel;
    @FXML protected Label strokeLabel;
    @FXML protected Label asthmaLabel;
    @FXML protected Label lungDiseaseLabel;
    @FXML protected Label seizureLabel;
    @FXML protected Label neurologicalLabel;
    @FXML protected Label hspRespiratoryCardioLabel;
    @FXML protected Label hspHeartLungLabel;
    @FXML protected Label medicationLabel;
    @FXML protected Label allergiesLabel;
    @FXML protected Label surgeryLabel;
    @FXML protected Label injuryLabel;

    /**
     * Updates the UI with the provided medical data.
     * If the data is null, an error message is displayed.
     *
     * @param data The `MedicalData` object containing the patient's medical information.
     */
    protected void updateUI(MedicalData data) {
        if (data == null) {
            showError("Error", "No patient data found");
            return;
        }

        // General information
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

        // Medical history
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

        // Additional information
        medicationLabel.setText(data.isPersc_med() ? "Yes" : "No");
        allergiesLabel.setText(data.isAllergies() ? "Yes" : "No");
        surgeryLabel.setText(data.isSurgery() ? "Yes" : "No");
        injuryLabel.setText(data.isSer_injury() ? "Yes" : "No");
    }
}