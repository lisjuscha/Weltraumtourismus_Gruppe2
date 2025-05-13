package com.example.flightprep.service;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.util.SessionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The `AppointmentService` class provides business logic for managing appointments
 * in the Flight Preparation application. It handles operations such as booking appointments,
 * validating time slots, and retrieving appointments for specific dates or weeks.
 * This class follows the Singleton design pattern to ensure a single instance.
 */
public class AppointmentService {
    private static AppointmentService instance;
    private static final Object LOCK = new Object();
    private static final String[] TIME_SLOTS = {"09:00", "11:00", "14:00", "16:00"};

    private final AppointmentDAO appointmentDAO;
    private final CustomerDAO customerDAO;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes the `AppointmentDAO` and `CustomerDAO` instances.
     */
    private AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Retrieves the single instance of `AppointmentService`.
     *
     * @return The `AppointmentService` instance.
     */
    public static AppointmentService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AppointmentService();
                }
            }
        }
        return instance;
    }

    /**
     * Books an appointment for the current user at a specific date and time.
     *
     * @param date The date of the appointment.
     * @param time The time of the appointment.
     * @throws SQLException If the time slot is invalid or a database error occurs.
     */
    public void bookAppointment(LocalDate date, String time) throws SQLException {
        synchronized (LOCK) {
            if (!isValidSlot(date, time)) {
                throw new SQLException("Der gewählte Termin ist nicht verfügbar");
            }

            String userId = SessionManager.getCurrentUserId();
            appointmentDAO.bookAppointment(userId, date, time);
            customerDAO.updateAppointmentStatus(userId);
        }
    }

    /**
     * Validates if a specific time slot on a given date is available.
     *
     * @param date The date to validate.
     * @param time The time slot to validate.
     * @return `true` if the slot is valid and available, otherwise `false`.
     * @throws SQLException If a database error occurs.
     */
    public boolean isValidSlot(LocalDate date, String time) throws SQLException {
        if (!isValidTimeSlot(time)) return false;

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime slotTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        String userId = SessionManager.getCurrentUserId();
        LocalDate flightDate = customerDAO.getFlightDate(userId);

        boolean isPastDateTime = date.isBefore(today) ||
                (date.isEqual(today) && slotTime.isBefore(currentTime));
        boolean isAfterMaxDate = date.isAfter(flightDate.minusDays(30));
        boolean isBooked = appointmentDAO.isSlotBooked(date, time);

        return !isPastDateTime && !isAfterMaxDate && !isBooked;
    }

    /**
     * Checks if a given time slot is valid.
     *
     * @param time The time slot to check.
     * @return `true` if the time slot is valid, otherwise `false`.
     */
    private boolean isValidTimeSlot(String time) {
        for (String slot : TIME_SLOTS) {
            if (slot.equals(time)) return true;
        }
        return false;
    }

    /**
     * Retrieves the available time slots for appointments.
     *
     * @return An array of available time slots.
     */
    public String[] getTimeSlots() {
        return TIME_SLOTS.clone();
    }

    /**
     * Retrieves a list of appointments for a specific date.
     *
     * @param date The date for which to retrieve appointments.
     * @return A list of `Appointment` objects for the specified date.
     * @throws SQLException If a database error occurs.
     */
    public List<Appointment> getAppointmentsForDate(LocalDate date) throws SQLException {
        synchronized (LOCK) {
            return appointmentDAO.getAppointmentsByDate(date);
        }
    }

    /**
     * Retrieves the available time slots for a specific date.
     *
     * @param date The date for which to retrieve available time slots.
     * @return A list of available time slots as strings.
     * @throws SQLException If a database error occurs.
     */
    public List<String> getAvailableTimeSlotsForDate(LocalDate date) throws SQLException {
        List<String> availableSlots = new ArrayList<>();
        for (String slot : TIME_SLOTS) {
            if (isValidSlot(date, slot)) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    /**
     * Retrieves the start date of the current week.
     *
     * @return The start date of the current week.
     */
    public LocalDate getCurrentWeekStart() {
        return LocalDate.now().with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1);
    }

    /**
     * Retrieves a list of appointments for the current week.
     *
     * @param weekStart The start date of the week.
     * @return A list of `Appointment` objects for the week.
     * @throws SQLException If a database error occurs.
     */
    public List<Appointment> getWeekAppointments(LocalDate weekStart) throws SQLException {
        synchronized (LOCK) {
            List<Appointment> weekAppointments = new ArrayList<>();
            LocalDate weekEnd = weekStart.plusDays(6);

            for (LocalDate date = weekStart; !date.isAfter(weekEnd); date = date.plusDays(1)) {
                weekAppointments.addAll(appointmentDAO.getAppointmentsByDate(date));
            }
            return weekAppointments;
        }
    }
    /**
     * Retrieves the color associated with a specific risk group.
     *
     * @param riskGroup The risk group value.
     * @return The color code as a string.
     */
    public String getRiskGroupColor(int riskGroup) {
        switch (riskGroup) {
            case 1:
                return "#90EE90"; // grün
            case 2:
                return "#FFD700"; // gelb
            case 3:
                return "#FFB6C6"; // rot
            default:
                return "white";
        }
    }

}