package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;
import com.example.flightprep.service.CustomerStatusService;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerPrepController extends CustomerController {
    private final CustomerStatusService customerStatusService;

    @FXML
    CheckBox medicalDataCheckBox;
    @FXML
    Button medicalDataButton;
    @FXML
    CheckBox appointmentCheckBox;
    @FXML
    Button appointmentButton;
    @FXML
    CheckBox uploadCheckBox;
    @FXML
    Button uploadButton;

    public CustomerPrepController() {
        this.customerStatusService = new CustomerStatusService();
    }

    @FXML
    public void initialize() {
        try {
            String currentUserId = SessionManager.getCurrentUserId();
            Customer customer = customerStatusService.getCustomerStatus(currentUserId);

            if (!customer.isFormSubmitted()) {
                appointmentButton.setDisable(true);
                appointmentButton.setVisible(false);
                uploadButton.setDisable(true);
                uploadButton.setVisible(false);
            } else {
                medicalDataCheckBox.setSelected(true);
                medicalDataButton.setDisable(true);
                medicalDataButton.setVisible(false);
                if (!customer.isAppointmentMade()) {
                    uploadButton.setDisable(true);
                    uploadButton.setVisible(false);
                } else {
                    appointmentCheckBox.setSelected(true);
                    appointmentButton.setDisable(true);
                    appointmentButton.setVisible(false);
                    if (customer.isFileUploaded()) {
                        uploadCheckBox.setSelected(true);
                        uploadButton.setDisable(true);
                        uploadButton.setVisible(false);
                    }
                }
            }
        } catch (RuntimeException e) {
            showError("Error", "Error loading customer status: " + e.getMessage());
        }
    }


    public void switchToSurvey(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerSurvey.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error", "Error switching to survey screen: " + e.getMessage());
        }
    }

    public void switchToAppointment(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerAppointment.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error", "Error switching to survey screen: " + e.getMessage());
        }
    }

    public void switchToUpload(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerUpload.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error", "Error switching to survey screen: " + e.getMessage());
        }
    }
}
