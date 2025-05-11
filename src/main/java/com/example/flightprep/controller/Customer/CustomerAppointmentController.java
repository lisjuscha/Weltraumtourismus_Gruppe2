package com.example.flightprep.controller.Customer;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.sql.Connection;
import java.sql.SQLException;

public class CustomerAppointmentController extends CustomerController {
    @FXML
    private Label weekLabel;
    @FXML
    private GridPane weekGrid;
    @FXML
    private Button prevWeekButton;
    @FXML
    private Button nextWeekButton;

    private LocalDate currentWeekStart;
    private LocalDate flightDate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final WeekFields weekFields = WeekFields.of(Locale.GERMANY);

    @FXML
    public void initialize() {
        try (Connection conn = DbConnection.getConnection()) {
            currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);

            UserDAO userDao = new UserDAO(conn);
            flightDate = userDao.getFlightDate(SessionManager.getCurrentUserId());

            if (flightDate == null) {
                System.err.println("No Date found for userID: " + SessionManager.getCurrentUserId());
                return;
            }

            updateWeekLabel();
            loadAppointments();
            updateNavigationButtons();

        } catch (SQLException e) {
            System.err.println("Error while initializing the dates:" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private void updateNavigationButtons() {
        LocalDate now = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        prevWeekButton.setDisable(currentWeekStart.isBefore(now) || currentWeekStart.isEqual(now));

        LocalDate maxDate = flightDate.minusDays(30);
        nextWeekButton.setDisable(currentWeekStart.plusWeeks(1).isAfter(maxDate));
    }

    private void updateWeekLabel() {
        LocalDate weekEnd = currentWeekStart.plusDays(4);
        int weekNumber = currentWeekStart.get(weekFields.weekOfWeekBasedYear());
        weekLabel.setText(String.format("KW %d (%s - %s)",
                weekNumber,
                currentWeekStart.format(formatter),
                weekEnd.format(formatter)));
    }

    private void loadAppointments() {
        try (Connection conn = DbConnection.getConnection()) {
            AppointmentDAO appointmentDao = new AppointmentDAO(conn);
            LocalDate today = LocalDate.now();

            // Clear previous buttons
            weekGrid.getChildren().removeIf(node -> node instanceof Button);

            // Slots für jeden Tag und jede Zeit erstellen
            for (int row = 1; row <= 4; row++) {
                for (int col = 1; col <= 5; col++) {
                    final LocalDate date = currentWeekStart.plusDays(col - 1);
                    final String time;
                    switch (row) {
                        case 1: time = "09:00"; break;
                        case 2: time = "11:00"; break;
                        case 3: time = "14:00"; break;
                        case 4: time = "16:00"; break;
                        default: time = ""; break;
                    }

                    Button slot = new Button();
                    slot.setMaxWidth(Double.MAX_VALUE);
                    slot.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setFillWidth(slot, true);
                    GridPane.setFillHeight(slot, true);

                    boolean isPastDateTime = date.isBefore(today) ||
                            (date.isEqual(today) && time.compareTo(java.time.LocalTime.now()
                                    .format(DateTimeFormatter.ofPattern("HH:mm"))) < 0);
                    boolean isAfterMaxDate = date.isAfter(flightDate.minusDays(30));

                    if (isPastDateTime || isAfterMaxDate || appointmentDao.isSlotBooked(date, time)) {
                        slot.setText("Not Available");
                        slot.getStyleClass().add("time-slot-occupied");
                        slot.setDisable(true);
                    } else {
                        slot.setText("Available");
                        slot.getStyleClass().add("time-slot");
                        slot.setOnAction(e -> handleSlotClick(date, time, e));
                    }

                    weekGrid.add(slot, col, row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while loading appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handlePreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel();
        loadAppointments();
        updateNavigationButtons();
    }

    @FXML
    private void handleNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel();
        loadAppointments();
        updateNavigationButtons();
    }

    private void handleSlotClick(LocalDate date, String time, ActionEvent originalEvent) {
        // Speichere die ursprüngliche Scene-Referenz
        Node source = (Node) originalEvent.getSource();
        Scene scene = source.getScene();
        Stage currentStage = (Stage) scene.getWindow();


        Alert alert = createAlert("Appointment Booking",
                "Do you want to book this appointment?",
                String.format("Date: %s\nTime: %s", date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), time),
                Alert.AlertType.CONFIRMATION);

        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Book");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Cancel");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DbConnection.getConnection()) {
                    AppointmentDAO appointmentDao = new AppointmentDAO(conn);
                    appointmentDao.bookAppointment(date, time);
                    conn.commit();
                    loadAppointments();

                    showSuccess("Booking Success", "Appointment booked successfully!");

                    // Nutze die gespeicherte Stage für den Szenenwechsel


                    SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", scene);

                } catch (SQLException e) {
                    showError("Booking Error", "The appointment could not be booked: " + e.getMessage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}