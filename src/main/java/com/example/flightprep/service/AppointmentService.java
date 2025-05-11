package com.example.flightprep.service;

import com.example.flightprep.dao.AppointmentDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.Appointment;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.util.SessionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentService {
    private final DatabaseConnection databaseConnection;
    private Connection connection;
    private AppointmentDAO appointmentDAO;
    private UserDAO userDAO;

    public AppointmentService() {
        this.databaseConnection = DatabaseFactory.getDatabase();
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            this.connection = databaseConnection.getConnection();
            this.appointmentDAO = new AppointmentDAO(connection);
            this.userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Initialisieren des AppointmentService", e);
        }
    }

    public LocalDate getFlightDate(String userId) throws SQLException {
        checkConnection();
        return userDAO.getFlightDate(userId);
    }

    public boolean isSlotAvailable(LocalDate date, String time) throws SQLException {
        checkConnection();
        return !appointmentDAO.isSlotBooked(date, time);
    }

    public void bookAppointment(LocalDate date, String time) throws SQLException {
        checkConnection();
        if (!isValidSlot(date, time)) {
            throw new SQLException("Der gewählte Termin ist nicht verfügbar");
        }
        appointmentDAO.bookAppointment(date, time);
        connection.commit();
    }

    public boolean isValidSlot(LocalDate date, String time) throws SQLException {
        checkConnection();
        LocalDate today = LocalDate.now();
        LocalDate flightDate = getFlightDate(SessionManager.getCurrentUserId());
        LocalTime currentTime = LocalTime.now();
        LocalTime slotTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));

        boolean isPastDateTime = date.isBefore(today) ||
                (date.isEqual(today) && slotTime.isBefore(currentTime));
        boolean isAfterMaxDate = date.isAfter(flightDate.minusDays(30));
        boolean isBooked = !isSlotAvailable(date, time);

        return !isPastDateTime && !isAfterMaxDate && !isBooked;
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) throws SQLException {
        checkConnection();
        return appointmentDAO.getAppointmentsByDate(date);
    }

    public void updateAppointmentStatus() throws SQLException {
        checkConnection();
        userDAO.setAppointmentStatus();
        connection.commit();
    }

    private void checkConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
    }

    public void cleanup() {
        if (connection != null) {
            databaseConnection.closeConnection(connection);
            connection = null;
        }
    }
}