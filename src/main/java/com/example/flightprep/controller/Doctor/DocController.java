package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.GenreralController;
import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class DocController extends GenreralController {

    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocHome.fxml", actionEvent);
        } catch (Exception e) {
            System.out.println("Error switching to home screen: " + e.getMessage());
        }
    }
    @FXML
    public void switchToCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocCalendar.fxml", actionEvent);
        } catch (Exception e) {
            System.out.println("Error switching to calendar: " + e.getMessage());
        }
    }
}
