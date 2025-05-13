package com.example.flightprep.controller.BasicController;

import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * The `CustomerController` class serves as a base controller for customer-related views in the application.
 * It provides common navigation functionality for switching between different customer screens, such as
 * the home screen, flight preparation screen, calendar, and flight details.
 * This class extends `GeneralController`.
 */
public abstract class CustomerController extends GeneralController {

    /**
     * Switches the scene to the customer home screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerHome.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to home screen: " + e.getMessage());
        }
    }

    /**
     * Switches the scene to the flight preparation screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToFlightPrep(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to preparation screen: " + e.getMessage());
        }
    }

    /**
     * Switches the scene to the calendar screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToCalendar(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to calendar screen: " + e.getMessage());
        }
    }

    /**
     * Switches the scene to the "My Flight" screen.
     *
     * @param actionEvent The `ActionEvent` triggered by the button click.
     */
    @FXML
    public void switchToMyFlight(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerMyFlight.fxml", actionEvent);
        } catch (Exception e) {
            showError("Error" , "Error switching to my flight screen: " + e.getMessage());

        }
    }
}
