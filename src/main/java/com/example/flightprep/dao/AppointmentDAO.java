package com.example.flightprep.dao;

import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppointmentDAO {
    private final Connection connection;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public AppointmentDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean isSlotBooked(LocalDate date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, date.format(formatter));
            stmt.setString(2, time);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private String getCurrentDoctorId() throws SQLException {
        String sql = "SELECT user_id FROM Doctor LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_id");
                }
                throw new SQLException("Kein Arzt in der Datenbank gefunden");
            }
        }
    }


    public void bookAppointment(LocalDate date, String time) throws SQLException {

        System.out.println("Versuche Termin zu buchen: " + date + " " + time);
        System.out.println("Customer ID: " + SessionManager.getCurrentUserId());
        System.out.println("Doctor ID: " + getCurrentDoctorId());
        // Zuerst prüfen ob der Termin bereits gebucht ist
        if (isSlotBooked(date, time)) {
            throw new SQLException("Dieser Termin ist bereits gebucht.");
        }

        // SQL mit autoincrement ID
        String sql = "INSERT INTO appointments (appointment_id, customer_id, doctor_id, date, time) " +
                "VALUES ((SELECT COALESCE(MAX(appointment_id), 0) + 1 FROM appointments), ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, SessionManager.getCurrentUserId());
            stmt.setString(2, getCurrentDoctorId());
            stmt.setString(3, date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            stmt.setString(4, time);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Termin konnte nicht gespeichert werden");
            }
            
            // Update appointment status
            UserDAO userDAO = new UserDAO(connection);
            userDAO.setAppointmentStatus();
            
        } catch (SQLException e) {
            throw new SQLException("Fehler beim Buchen des Termins: " + e.getMessage());
        }
    }
}
