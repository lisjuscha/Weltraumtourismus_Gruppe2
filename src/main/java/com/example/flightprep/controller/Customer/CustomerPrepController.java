package com.example.flightprep.controller.Customer;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class CustomerPrepController extends CustomerController {
    @FXML
    CheckBox medicalDataCheckBox;
    CheckBox appointmentCheckBox;
    CheckBox uploadCheckBox;

    public void switchToSurvey(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerSurvey.fxml", actionEvent, true);
        } catch (Exception e) {
            System.out.println("Error switching to survey screen: " + e.getMessage());
        }
    }
}
