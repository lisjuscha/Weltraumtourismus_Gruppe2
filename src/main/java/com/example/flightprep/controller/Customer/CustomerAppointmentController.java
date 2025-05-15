package com.example.flightprep.controller.Customer;

import com.example.flightprep.controller.BasicController.CustomerController;
import com.example.flightprep.service.AppointmentService;
import com.example.flightprep.service.CustomerService;
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

/**
 * The `CustomerAppointmentController` class manages the appointment booking view for customers in the application.
 * It allows customers to view available time slots, navigate through weeks, and book appointments for their flight preparation.
 * This class extends `CustomerController`.
 */
public class CustomerAppointmentController extends CustomerController {
    private AppointmentService appointmentService;
    private CustomerService customerService;
    private LocalDate currentWeekStart;
    private LocalDate flightDate;

    @FXML private Label weekLabel;
    @FXML private GridPane weekGrid;
    @FXML private Button prevWeekButton;
    @FXML private Button nextWeekButton;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final WeekFields weekFields = WeekFields.of(Locale.GERMANY);

    /**
     * Constructs a new `CustomerAppointmentController` and initializes the required services.
     */
    public CustomerAppointmentController() {
        this.appointmentService = AppointmentService.getInstance();
        this.customerService = CustomerService.getInstance();
    }

    /**
     * Initializes the appointment booking view by setting up dates, loading appointments, and updating navigation buttons.
     * This method is called automatically after the FXML file has been loaded.
     */
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

    /**
     * Initializes the current week and retrieves the flight date for the current user.
     *
     * @throws SQLException If an error occurs while retrieving the flight date.
     */
    private void initializeDates() throws SQLException {
        currentWeekStart = LocalDate.now().with(weekFields.dayOfWeek(), 1);
        String userId = SessionManager.getCurrentUserId();
        if (userId != null) {
            flightDate = customerService.getFlightDate(userId);
        }
    }

    /**
     * Loads the available and unavailable appointment slots for the current week into the grid.
     */
    private void loadAppointments() {
        // Clear previous week's appointment slots before repopulating
        weekGrid.getChildren().removeIf(node -> node instanceof Button);
        for (int row = 1; row <= 4; row++) {
            for (int col = 1; col <= 5; col++) {
                createTimeSlot(row, col);
            }
        }
    }

    /**
     * Creates a time slot button for a specific row and column in the grid.
     *
     * @param row The row index of the grid.
     * @param col The column index of the grid.
     */
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

    /**
     * Configures a time slot button as unavailable.
     *
     * @param slot The button representing the time slot.
     */
    private void configureUnavailableSlot(Button slot) {
        slot.setText("Not Available");
        slot.getStyleClass().add("time-slot-occupied");
        slot.setDisable(true);
    }

    /**
     * Configures a time slot button as available and sets up its click event handler.
     *
     * @param slot The button representing the time slot.
     * @param date The date of the time slot.
     * @param time The time of the time slot.
     */
    private void configureAvailableSlot(Button slot, LocalDate date, String time) {
        slot.setText("Available");
        slot.getStyleClass().add("time-slot");
        slot.setOnAction(e -> handleSlotClick(date, time, e));
    }

    /**
     * Updates the navigation buttons to enable or disable them based on the current week and flight date.
     */
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

    /**
     * Updates the label displaying the current week and its date range.
     */
    private void updateWeekLabel() {
        LocalDate weekEnd = currentWeekStart.plusDays(4);
        int weekNumber = currentWeekStart.get(weekFields.weekOfWeekBasedYear());
        weekLabel.setText(String.format("Week %d (%s - %s)",
                weekNumber,
                currentWeekStart.format(formatter),
                weekEnd.format(formatter)));
    }

    /**
     * Handles the action of navigating to the previous week.
     */
    @FXML
    private void handlePreviousWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel();
        loadAppointments();
        updateNavigationButtons();
    }

    /**
     * Handles the action of navigating to the next week.
     */
    @FXML
    private void handleNextWeek() {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel();
        loadAppointments();
        updateNavigationButtons();
    }

    /**
     * Handles the action of booking an appointment when a time slot is clicked.
     *
     * @param date The date of the selected time slot.
     * @param time The time of the selected time slot.
     * @param event The `ActionEvent` triggered by the button click.
     */
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
                    SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerCalendar.fxml", scene);
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

    protected void updateWeekLabelForTest() {
       updateWeekLabel();
    }

}