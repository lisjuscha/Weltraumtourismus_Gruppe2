package com.example.flightprep.controller.Doctor;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class DocController {

    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocHome", actionEvent, true);
        } catch (Exception e) {
            System.out.println("Error switching to home screen: " + e.getMessage());
        }
    }
    @FXML
    public void switchCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreen/DocCalendar.fxml", actionEvent, true);
        } catch (Exception e) {
            System.out.println("Error switching to home screen: " + e.getMessage());
        }
    }
}
