package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.util.SessionManager;
import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The `CustomerCalendarController` class manages the customer's calendar view.
 * It displays the customer's flight date and their scheduled medical appointment details.
 * It also provides options to navigate to screens for changing these dates.
 * This class extends `CustomerController`.
 */
public class CustomerCalendarController extends CustomerController {

    @FXML
    private Label flightDateLabel;

    @FXML
    private Label appointmentDateLabel;

    @FXML
    private Label appointmentTimeLabel;

    @FXML
    private Label appointmentDoctorLabel;

    private CustomerService customerService;
    private AppointmentService appointmentService;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     * It sets up the necessary services and loads the calendar data for the current user.
     */
    @FXML
    public void initialize() {
        customerService = CustomerService.getInstance();
        appointmentService = AppointmentService.getInstance(); 

        loadCalendarData();
    }

    /**
     * Loads the flight date and appointment data for the currently logged-in user
     * and updates the corresponding labels in the UI.
     * Handles potential errors during data fetching by displaying error messages.
     */
    private void loadCalendarData() {
        String userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            showError("Error", "User session not found. Please log in again.");
            flightDateLabel.setText("Error loading data");
            appointmentDateLabel.setText("Error loading data");
            appointmentTimeLabel.setText("");
            appointmentDoctorLabel.setText("");
            return;
        }

        // Load Flight Date
        try {
            LocalDate flightDate = customerService.getFlightDate(userId);
            if (flightDate != null) {
                flightDateLabel.setText(flightDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            } else {
                flightDateLabel.setText("Not set");
            }
        } catch (SQLException e) {
            flightDateLabel.setText("Error loading flight date");
            
        }

        // Load Appointment Data
        try {
            Appointment appointment = appointmentService.getAppointmentByCustomerId(userId);

            if (appointment != null) {
                appointmentDateLabel.setText(appointment.getDate()); 
                appointmentTimeLabel.setText(appointment.getTime());
                appointmentDoctorLabel.setText("ID: " + appointment.getDoctorId());
            } else {
                appointmentDateLabel.setText("Not booked");
                appointmentTimeLabel.setText("-");
                appointmentDoctorLabel.setText("-");
            }
        } catch (SQLException e) { 
            appointmentDateLabel.setText("Error loading appointment");
            appointmentTimeLabel.setText("");
            appointmentDoctorLabel.setText("");
            showError("Database Error", "Could not load appointment details: " + e.getMessage());
        }
    }

    /**
     * Handles the action event for the "Change Appointment" button.
     * Navigates the user to the appointment booking/changing screen.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleChangeAppointment(ActionEvent event) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerAppointment.fxml", event);
        } catch (IOException e) {
            showError("Navigation Error", "Could not open the appointment booking screen: " + e.getMessage());
        }
    }
    
    /**
     * Handles the action event for the "Change Flight Date" button.
     * Navigates the user to the screen for changing their flight date.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleChangeFlightDate(ActionEvent event) {
        try {
            SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerChangeFlightDate.fxml", event);
        } catch (IOException e) {
            showError("Navigation Error", "Could not open the change flight date screen: " + e.getMessage());
        }
    }
} 