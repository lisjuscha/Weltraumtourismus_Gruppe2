package com.example.flightprep.controller.BasicController;

import javafx.scene.control.Alert;

/**
 * The `GeneralController` class serves as a base controller for all controllers in the application.
 * It provides utility methods for creating and displaying alert dialogs, such as success and error messages.
 * This class is designed to be extended by other controllers to inherit common functionality.
 */
public abstract class GeneralController {

    /**
     * Creates an alert dialog with the specified title, header, message, and alert type.
     *
     * @param title     The title of the alert dialog.
     * @param header    The header text of the alert dialog (can be null).
     * @param message   The content message of the alert dialog.
     * @param alertType The type of the alert (e.g., INFORMATION, ERROR, WARNING).
     * @return The created `Alert` object.
     */
    public Alert createAlert(String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);          
        alert.setHeaderText(header);          
        alert.setContentText(message);     
        return alert;
    }
    /**
     * Displays a success message in an information alert dialog.
     *
     * @param title   The title of the success dialog.
     * @param content The content message of the success dialog.
     */
    public void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an error message in an error alert dialog.
     *
     * @param title   The title of the error dialog.
     * @param content The content message of the error dialog.
     */
    public void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
