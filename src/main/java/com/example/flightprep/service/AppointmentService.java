package com.example.flightprep.service;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.CustomerDAO;
import com.example.flightprep.dao.UserDAO;
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


public class AppointmentService {
    private static AppointmentService instance;
    private static final Object LOCK = new Object();
    private static final String[] TIME_SLOTS = {"09:00", "11:00", "14:00", "16:00"};

    private final AppointmentDAO appointmentDAO;
    private final CustomerDAO customerDAO;

    private AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.customerDAO = new CustomerDAO();
    }

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

    private boolean isValidTimeSlot(String time) {
        for (String slot : TIME_SLOTS) {
            if (slot.equals(time)) return true;
        }
        return false;
    }

    public String[] getTimeSlots() {
        return TIME_SLOTS.clone();
    }

    public List<Appointment> getAppointmentsForDate(LocalDate date) throws SQLException {
        synchronized (LOCK) {
            return appointmentDAO.getAppointmentsByDate(date);
        }
    }

    public List<String> getAvailableTimeSlotsForDate(LocalDate date) throws SQLException {
        List<String> availableSlots = new ArrayList<>();
        for (String slot : TIME_SLOTS) {
            if (isValidSlot(date, slot)) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    public LocalDate getCurrentWeekStart() {
        return LocalDate.now().with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1);
    }

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