package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.PatientDataDisplayController;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.service.CustomerService;

import java.sql.SQLException;

/**
 * The `DocPatientDataController` class manages the patient data view for doctors in the application.
 * It allows doctors to view, edit, and manage the medical data of their assigned patients.
 */
public class DocPatientDataController extends PatientDataDisplayController {
    private final CustomerService customerService;

    public DocPatientDataController() {
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Loads the medical data for the selected patient.
     * Retrieves the data from the database and populates the view with the patient's details.
     */
    public void loadPatientData(String patientId) {
        try {
            MedicalData data = customerService.getMedicalData(patientId);
            updateUI(data);
        } catch (SQLException e) {
            showError("Error", "Fehler beim Laden der Patientendaten: " + e.getMessage());
        }
    }
}