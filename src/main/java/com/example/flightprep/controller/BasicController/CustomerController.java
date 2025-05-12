package com.example.flightprep.controller.BasicController;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class CustomerController extends GeneralController {

    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerHome.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to home screen: " + e.getMessage());
        }
    }

    @FXML
    public void switchToFlightPrep(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to preparation screen: " + e.getMessage());
        }
    }

    @FXML
    public void switchToCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to calendar screen: " + e.getMessage());
        }
    }

    @FXML
    public void switchToMyFlight(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerMyFlight.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to my flight screen: " + e.getMessage());

        }
    }
}
