package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.PatientDataDisplayController;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;


import java.sql.SQLException;

public class DocPatientDataController extends PatientDataDisplayController {
    private final CustomerService customerService;

    public DocPatientDataController() {
        this.customerService = CustomerService.getInstance();
    }

    public void loadPatientData(String patientId) {
        try {
            MedicalData data = customerService.getMedicalData(patientId);
            updateUI(data);
        } catch (SQLException e) {
            showError("Error", "Fehler beim Laden der Patientendaten: " + e.getMessage());
        }
    }
}