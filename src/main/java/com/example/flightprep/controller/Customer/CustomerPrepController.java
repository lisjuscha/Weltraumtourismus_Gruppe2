package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerPrepController extends CustomerController {
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

    @FXML
    public void initialize() {
        try {
            // Get the current user ID from the session & establish a database connection
            String currentUserId = SessionManager.getCurrentUserId();
            Connection conn = DbConnection.getConnection();
            UserDAO userDao = new UserDAO(conn);

            try {
                // Check if the user has submitted the medical data form
                boolean hasSubmittedForm = userDao.getFormSubmittedStatus(currentUserId);
                boolean hasAppointment = userDao.getAppointmentStatus(currentUserId);
                boolean hasUploaded = userDao.getUploadStatus(currentUserId);
                // Set the checkbox and button state based on the form submission status
                if (!hasSubmittedForm) {
                    appointmentButton.setDisable(true);
                    appointmentButton.setVisible(false);
                    uploadButton.setDisable(true);
                    uploadButton.setVisible(false);
                } else {
                    medicalDataCheckBox.setSelected(true);
                    medicalDataButton.setDisable(true);
                    medicalDataButton.setVisible(false);
                    if (!hasAppointment) {
                        uploadButton.setDisable(true);
                        uploadButton.setVisible(false);
                    } else {
                        appointmentCheckBox.setSelected(true);
                        appointmentButton.setDisable(true);
                        appointmentButton.setVisible(false);
                        if (hasUploaded) {
                            uploadCheckBox.setSelected(true);
                            uploadButton.setDisable(true);
                            uploadButton.setVisible(false);
                        }
                    }
                }
            } finally {
                DbConnection.closeConnection(conn);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    public void switchToSurvey(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerSurvey.fxml", actionEvent);
        } catch (Exception e) {
            System.out.println("Error switching to survey screen: " + e.getMessage());
        }
    }
    public void switchToAppointment(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerAppointment.fxml", actionEvent);
        } catch (Exception e) {
            System.out.println("Error switching to appointment screen: " + e.getMessage());
        }
    }
    public void switchToUpload(ActionEvent actionEvent) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerUpload.fxml", actionEvent);
        } catch (Exception e) {
            System.out.println("Error switching to upload screen: " + e.getMessage());
        }
    }
}
