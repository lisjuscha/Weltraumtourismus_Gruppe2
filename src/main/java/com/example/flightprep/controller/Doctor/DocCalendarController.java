package com.example.flightprep.controller.Doctor;

import com.example.flightprep.controller.BasicController.DocController;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.service.AppointmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * The `DocCalendarController` class manages the calendar view for doctors in the application.
 * It allows doctors to view and manage their appointments and schedules.
 */

public class DocCalendarController extends DocController implements Initializable {
    @FXML private VBox appointmentsContainer;
    @FXML private Label weekLabel;


    private LocalDate currentWeekStart;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final AppointmentService appointmentService ;

    /**
     * Constructs a new `DocCalendarController` instance.
     * Initializes the `AppointmentService` for managing appointment data.
     */
    public DocCalendarController() {
        this.appointmentService = AppointmentService.getInstance();
    }

    /**
     * Initializes the calendar view and sets up necessary event handlers.
     * This method is called automatically after the FXML file has been loaded.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentWeekStart = appointmentService.getCurrentWeekStart();
        loadAppointments();
    }

    /**
     * Loads the appointments for the currently logged-in doctor.
     * Retrieves the data from the database and populates the calendar view.
     */
    private void loadAppointments() {
        appointmentsContainer.getChildren().clear();
        try {
            List<Appointment> weekAppointments = appointmentService.getWeekAppointments(currentWeekStart);

            for (int i = 0; i < 7; i++) {
                LocalDate date = currentWeekStart.plusDays(i);
                VBox dayContainer = createDayContainer(date);

                List<Appointment> dayAppointments = new ArrayList<>();
                for (Appointment weekAppointment : weekAppointments) {
                    if (weekAppointment.getDate().equals(date.format(formatter))) {
                        dayAppointments.add(weekAppointment);
                    }
                }

                for (Appointment apt : dayAppointments) {
                    HBox appointmentBox = createAppointmentBox(apt);
                    dayContainer.getChildren().add(appointmentBox);
                }

                appointmentsContainer.getChildren().add(dayContainer);
            }

            updateWeekLabel();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Error loading appointments");
        }
    }


    private VBox createDayContainer(LocalDate date) {
        VBox dayContainer = new VBox(10);
        dayContainer.setPadding(new Insets(10));
        dayContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label dateLabel = new Label(date.format(formatter));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        dayContainer.getChildren().add(dateLabel);

        return dayContainer;
    }

    private HBox createAppointmentBox(Appointment appointment) {
        HBox appointmentBox = new HBox(10);
        appointmentBox.setPadding(new Insets(5));
        appointmentBox.setStyle("-fx-background-color: " +
                appointmentService.getRiskGroupColor(appointment.getRiskGroup()) +
                "; -fx-border-radius: 3;");

        Label timeLabel = new Label(appointment.getTime());
        timeLabel.setStyle("-fx-font-size: 14px;");

        Label customerLabel = new Label(String.format("Patient: %s %s (Risk Group: %d)",
                appointment.getCustomerFirstName(),
                appointment.getCustomerLastName(),
                appointment.getRiskGroup()));

        Button viewButton = createViewButton(appointment.getCustomerId());

        appointmentBox.getChildren().addAll(timeLabel, customerLabel, viewButton);
        return appointmentBox;
    }

    private Button createViewButton(String customerId) {
        Button viewButton = new Button("View Survey");
        viewButton.getStyleClass().add("view-details-button");
        viewButton.setOnAction(e -> openPatientData(customerId, e));
        return viewButton;
    }

    private void openPatientData(String customerId, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/flightprep/DocScreens/DocPatientData.fxml"));
            Parent root = loader.load();

            DocPatientDataController controller = loader.getController();
            controller.loadPatientData(customerId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(
                    "/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void previousWeek(ActionEvent actionEvent) {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        loadAppointments();
    }

    @FXML
    public void nextWeek(ActionEvent actionEvent) {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        loadAppointments();
    }

    private void updateWeekLabel() {
        String weekText = currentWeekStart.format(formatter) + " - " +
                currentWeekStart.plusDays(6).format(formatter);
        weekLabel.setText(weekText);
        weekLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    }
}