package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.model.Customer;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.sql.SQLException;

public class CustomerPrepController extends CustomerController {
    private final CustomerService customerService;

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
        this.customerService = CustomerService.getInstance();
    }

    @FXML
    public void initialize() {
        try {
            String currentUserId = SessionManager.getCurrentUserId();
            Customer customer = customerService.getCustomerStatus(currentUserId);

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
        } catch (SQLException e) {
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
