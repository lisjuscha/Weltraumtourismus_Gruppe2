package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
import com.example.flightprep.service.UserService;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class CustomerAppointmentController extends CustomerController {
    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private LocalDate currentWeekStart;
    private LocalDate flightDate;

    @FXML private Label weekLabel;
    @FXML private GridPane weekGrid;
    @FXML private Button prevWeekButton;
    @FXML private Button nextWeekButton;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final WeekFields weekFields = WeekFields.of(Locale.GERMANY);

    public CustomerAppointmentController() {
        this.appointmentService = AppointmentService.getInstance();
        this.customerService = CustomerService.getInstance();
    }

    @FXML
    public void initialize() {
        try {
            initializeDates();
            if (flightDate == null) {
                showError("Initialization Error", "No flight date found for current user");
                return;
            }
            updateWeekLabel();
            loadAppointments();
            updateNavigationButtons();
        } catch (SQLException e) {
            showError("Initialization Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    private void initializeDates() throws SQLException {
        currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        String userId = SessionManager.getCurrentUserId();
        if (userId != null) {
            flightDate = customerService.getFlightDate(userId);
        }
    }

    private void loadAppointments() {
        weekGrid.getChildren().removeIf(node -> node instanceof Button);
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 5; col++) {
                createTimeSlot(row, col);
            }
        }
    }

    private void createTimeSlot(int row, int col) {
        LocalDate date = currentWeekStart.plusDays(col - 1);
        String time;
        switch (row) {
            case 1:
                time = "09:00";
                break;
            case 2:
                time = "11:00";
                break;
            case 3:
                time = "14:00";
                break;
            case 4:
                time = "16:00";
                break;
            default:
                time = "";
                break;
        }

        Button slot = new Button();
        slot.setMaxWidth(Double.MAX_VALUE);
        slot.setMaxHeight(Double.MAX_VALUE);
        GridPane.setFillWidth(slot, true);
        GridPane.setFillHeight(slot, true);

        try {
            if (time.isEmpty()) {
                configureUnavailableSlot(slot);
            }
            else if (!appointmentService.isValidSlot(date, time)) {
                configureUnavailableSlot(slot);
            } else {
                configureAvailableSlot(slot, date, time);
            }
        } catch (SQLException e) {
            showError("Error", "Failed to check slot availability: " + e.getMessage());
            configureUnavailableSlot(slot);
        }

        weekGrid.add(slot, col, row);
    }

    private void configureUnavailableSlot(Button slot) {
        slot.setText("Not Available");
        slot.getStyleClass().add("time-slot-occupied");
        slot.setDisable(true);
    }

    private void configureAvailableSlot(Button slot, LocalDate date, String time) {
        slot.setText("Available");
        slot.getStyleClass().add("time-slot");
        slot.setOnAction(e -> handleSlotClick(date, time, e));
    }

    private void updateNavigationButtons() {
        if (flightDate == null) {
            prevWeekButton.setDisable(true);
            nextWeekButton.setDisable(true);
            return;
        }
        LocalDate now = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        prevWeekButton.setDisable(currentWeekStart.isBefore(now) || currentWeekStart.isEqual(now));

        LocalDate maxDate = flightDate.minusDays(30);
        nextWeekButton.setDisable(currentWeekStart.plusWeeks(1).isAfter(maxDate));
    }

    private void updateWeekLabel() {
        LocalDate weekEnd = currentWeekStart.plusDays(4);
        int weekNumber = currentWeekStart.get(weekFields.weekOfWeekBasedYear());
        weekLabel.setText(String.format("Week %d (%s - %s)",
                weekNumber,
                currentWeekStart.format(formatter),
                weekEnd.format(formatter)));
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

    private void handleSlotClick(LocalDate date, String time, ActionEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();

        Alert alert = createAlert("Appointment Booking",
                "Do you want to book this appointment?",
                String.format("Date: %s\nTime: %s", date.format(formatter), time),
                Alert.AlertType.CONFIRMATION);

        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Book");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Cancel");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    appointmentService.bookAppointment(date, time);
                    showSuccess("Booking Success", "Appointment booked successfully!");
                    SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", scene);
                } catch (SQLException e) {
                    showError("Booking Error", "Failed to book appointment: " + e.getMessage());
                } catch (IOException e) {
                    showError("Navigation Error", "Failed to switch scene: " + e.getMessage());
                }
            }
        });
    }

    // Getter methods for testing
    protected GridPane getWeekGrid() {
        return weekGrid;
    }

    protected AppointmentService getAppointmentService() {
        return appointmentService;
    }

    protected Label getWeekLabel() {
        return weekLabel;
    }

    protected Button getPrevWeekButton() {
        return prevWeekButton;
    }

    protected Button getNextWeekButton() {
        return nextWeekButton;
    }

    protected LocalDate getFlightDate() {
        return flightDate;
    }

    protected LocalDate getCurrentWeekStart() {
        return currentWeekStart;
    }

    protected void initializeDatesForTest() throws SQLException {
        initializeDates();
    }

    protected void configureUnavailableSlotForTest(Button slot) {
        configureUnavailableSlot(slot);
    }

    protected void configureAvailableSlotForTest(Button slot, LocalDate date, String time) {
        configureAvailableSlot(slot, date, time);
    }

    protected Button createTimeSlotForTest(int row, int col) {
        Button slot = new Button();
        weekGrid.add(slot, col, row);

        LocalDate date = currentWeekStart.plusDays(col - 1);
        String time;
        switch (row) {
            case 1: time = "09:00"; break;
            case 2: time = "11:00"; break;
            case 3: time = "14:00"; break;
            case 4: time = "16:00"; break;
            default: time = ""; break;
        }

        slot.setMaxWidth(Double.MAX_VALUE);
        slot.setMaxHeight(Double.MAX_VALUE);
        GridPane.setFillWidth(slot, true);
        GridPane.setFillHeight(slot, true);

        try {
            if (time.isEmpty() || !appointmentService.isValidSlot(date, time)) {
                configureUnavailableSlot(slot);
            } else {
                configureAvailableSlot(slot, date, time);
            }
        } catch (SQLException e) {
            configureUnavailableSlot(slot);
        }

        return slot;
    }

    protected void handlePreviousWeekForTest() {
        handlePreviousWeek();
    }

    protected void handleNextWeekForTest() {
        handleNextWeek();
    }

    protected void handleSlotClickForTest(LocalDate date, String time, ActionEvent event) throws SQLException, IOException {
        handleSlotClick(date, time, event);
    }

    protected void updateNavigationButtonsForTest() {
        updateNavigationButtons();
    }

}