package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The `CustomerChangeFlightDateController` class manages the UI for customers to change their flight date.
 * It allows users to select a new flight date, validates it against business rules (e.g., not in the past,
 * and at least 30 days after any existing medical appointment), and saves the changes.
 * This class extends `CustomerController`.
 */
public class CustomerChangeFlightDateController extends CustomerController {

    @FXML
    private DatePicker newFlightDatePicker;
    @FXML
    private Label currentFlightDateLabel;
    @FXML
    private Label appointmentInfoLabel;

    private CustomerService customerService;
    private AppointmentService appointmentService;
    private LocalDate originalFlightDate;
    private Appointment currentAppointment;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     * It sets up services and loads the current flight and appointment data for the user.
     */
    @FXML
    public void initialize() {
        customerService = CustomerService.getInstance();
        appointmentService = AppointmentService.getInstance();
        loadCurrentData();
    }

    /**
     * Loads the current flight date and any existing medical appointment information for the logged-in user.
     * This data is then displayed in the UI to provide context for the user.
     */
    private void loadCurrentData() {
        String userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            showError("User Session Error", "User session not found.");
            return;
        }
        try {
            originalFlightDate = customerService.getFlightDate(userId);
            if (originalFlightDate != null) {
                currentFlightDateLabel.setText("Current Flight Date: " + originalFlightDate.format(DATE_FORMATTER));
                newFlightDatePicker.setValue(originalFlightDate);
            } else {
                currentFlightDateLabel.setText("Current Flight Date: Not set");
            }

            currentAppointment = appointmentService.getAppointmentByCustomerId(userId);
            if (currentAppointment != null && currentAppointment.getDate() != null && !currentAppointment.getDate().isEmpty()) {
                appointmentInfoLabel.setText("Medical Appointment: " + currentAppointment.getDate() + " at " + currentAppointment.getTime());
            } else {
                appointmentInfoLabel.setText("Medical Appointment: Not booked");
            }
        } catch (SQLException e) {
            showError("Data Load Error", "Error loading current data: " + e.getMessage());
        }
    }

    /**
     * Handles the action event for the "Save Changes" button.
     * It retrieves the selected new flight date, validates it against business rules
     * (e.g., not in the past, at least 30 days after an existing medical appointment),
     * and then attempts to save the new flight date via the `CustomerService`.
     * Feedback is provided to the user via alert dialogs.
     * On successful update, navigates back to the customer calendar screen.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String userId = SessionManager.getCurrentUserId();
        LocalDate newDate = newFlightDatePicker.getValue();

        if (userId == null) {
            showError("Session Error", "User session expired. Please log in again.");
            return;
        }
        if (newDate == null) {
            showError("Input Error", "Please select a new flight date.");
            return;
        }

        try {
            customerService.updateFlightDateWithValidation(userId, newDate);
            showSuccess("Success", "Flight date updated successfully!");
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", event);

        } catch (IllegalArgumentException e) {
            showError("Validation Error", e.getMessage());
        } catch (SQLException e) {
            showError("Database Error", "Database error: Could not update flight date. " + e.getMessage());
        } catch (IOException e) {
            showError("Navigation Error", "Navigation error: " + e.getMessage());
        }
    }

    /**
     * Handles the action event for the "Cancel" button.
     * Navigates the user back to the customer calendar screen without saving any changes.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", event);
        } catch (IOException e) {
            showError("Navigation Error", "Error navigating back: " + e.getMessage());
        }
    }
} 