package com.example.flightprep.controller.BasicController;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * The `DocController` class serves as a base controller for doctor-related views in the application.
 * It provides common navigation functionality for switching between different doctor screens, such as
 * the home screen, calendar, and patient management screen.
 * This class extends `GeneralController`.
 */
public abstract class DocController extends GeneralController {

    /**
     * Switches the scene to the doctor home screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocHome.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to home screen: " + e.getMessage());
        }
    }
    /**
     * Switches the scene to the doctor calendar screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocCalendar.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to calendar screen: " + e.getMessage());
        }
    }
    /**
     * Switches the scene to the doctor patients screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    public void switchToPatients(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/DocScreens/DocPatients.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to patients screen: " + e.getMessage());
        }
    }
}
