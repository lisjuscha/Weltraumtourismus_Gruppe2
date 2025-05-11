package com.example.flightprep.controller.Doctor;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DocCalendarController extends DocController implements Initializable {
    @FXML
    private VBox appointmentsContainer;
    @FXML
    private Label weekLabel;

    private LocalDate currentWeekStart = LocalDate.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentsContainer.getChildren().clear();

        try (Connection conn = DbConnection.getConnection()) {
            AppointmentDAO appointmentDao = new AppointmentDAO(conn);

            for (int i = 0; i < 7; i++) {
                LocalDate date = currentWeekStart.plusDays(i);
                List<Appointment> dayAppointments = appointmentDao.getAppointmentsByDate(date);

                VBox dayContainer = new VBox(10);
                dayContainer.setPadding(new Insets(10));
                dayContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

                Label dateLabel = new Label(date.format(formatter));
                dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                dayContainer.getChildren().add(dateLabel);

                for (Appointment appointment : dayAppointments) {
                    HBox appointmentBox = new HBox(10);
                    appointmentBox.setPadding(new Insets(5));

                    String boxColor;
                    switch (appointment.getRiskGroup()) {
                        case 1: boxColor = "#90EE90"; break; // green
                        case 2: boxColor = "#FFD700"; break; // yellow
                        case 3: boxColor = "#FFB6C6"; break; // red
                        default: boxColor = "white";
                    }
                    appointmentBox.setStyle("-fx-background-color: " + boxColor + "; -fx-border-radius: 3;");

                    Label timeLabel = new Label(appointment.getTime());
                    timeLabel.setStyle("-fx-font-size: 14px;");

                    Label customerLabel = new Label(String.format("Patient: %s %s (Risk Group: %d)",
                            appointment.getCustomerFirstName(),
                            appointment.getCustomerLastName(),
                            appointment.getRiskGroup()));

                    Button viewButton = new Button("View Survey");
                    viewButton.setStyle("-fx-font-size: 14px;");
                    viewButton.setOnAction(e -> openPatientData(appointment.getCustomerId(), e));

                    appointmentBox.getChildren().addAll(timeLabel, customerLabel, viewButton);
                    dayContainer.getChildren().add(appointmentBox);
                }

                appointmentsContainer.getChildren().add(dayContainer);
            }

            updateWeekLabel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openPatientData(String customerId, ActionEvent event) {
        try {
            // FXML-Datei laden
            String fxmlFile = "/com/example/flightprep/DocScreens/DocPatientData.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Controller holen und Daten laden
            DocPatientDataController controller = loader.getController();
            controller.loadPatientData(customerId);

            // Existierende Stage holen und Scene wechseln
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // CSS hinzufügen falls nötig
            scene.getStylesheets().add(getClass().getResource("/com/example/flightprep/Stylesheets/Prep.css").toExternalForm());
            stage.setScene(scene);
            //stage.setFullScreen(true);
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
