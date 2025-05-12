package com.example.flightprep.controller.BasicController;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class DocController extends GeneralController {

    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocHome.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to home screen: " + e.getMessage());
        }
    }
    @FXML
    public void switchToCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocCalendar.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to calendar screen: " + e.getMessage());
        }
    }
    public void switchToPatients(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocPatients.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to patients screen: " + e.getMessage());
        }
    }
}
