package com.example.flightprep.controller.Doctor;

import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.DbConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.Connection;

public class DocPatientDataController extends DocController {
    @FXML private Label heightLabel;
    @FXML private Label weightLabel;
    @FXML private Label smokingLabel;
    @FXML private Label alcoholLabel;
    @FXML private Label trainingLabel;
    @FXML private Label disabilityLabel;
    @FXML private Label disabilityDetailsLabel;

    @FXML private Label heartDiseaseLabel;
    @FXML private Label bloodPressureLabel;
    @FXML private Label heartbeatLabel;
    @FXML private Label strokeLabel;
    @FXML private Label asthmaLabel;
    @FXML private Label lungDiseaseLabel;
    @FXML private Label seizureLabel;
    @FXML private Label neurologicalLabel;
    @FXML private Label hspRespiratoryCardioLabel;
    @FXML private Label hspHeartLungLabel;

    @FXML private Label medicationLabel;
    @FXML private Label allergiesLabel;
    @FXML private Label surgeryLabel;
    @FXML private Label injuryLabel;

    public void loadPatientData(String patientId) {
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
}