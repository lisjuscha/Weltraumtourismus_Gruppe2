package com.example.flightprep.controller.BasicController;

import javafx.scene.control.Alert;

public abstract class GeneralController {

    public Alert createAlert(String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);              // Title of the alert box
        alert.setHeaderText(header);          // Remove the header (optional)
        alert.setContentText(message);     // The content of the alert (the error message)
        // Show the alert and wait for the user to close it
        return alert;
    }
    public void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
