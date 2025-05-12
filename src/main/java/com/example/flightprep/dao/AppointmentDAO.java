package com.example.flightprep.dao;


import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    private final DatabaseConnection databaseConnection;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public AppointmentDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public boolean isSlotBooked(LocalDate date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, date.format(formatter));
            stmt.setString(2, time);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean isBooked = rs.next() && rs.getInt(1) > 0;
                connection.commit();
                return isBooked;
            }
        }
    }

    public void bookAppointment(String customerId, LocalDate date, String time) throws SQLException {
        String sql = "INSERT INTO appointments (appointment_id, customer_id, doctor_id, date, time) " +
                "VALUES ((SELECT COALESCE(MAX(appointment_id), 0) + 1 FROM appointments), ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            stmt.setString(2, getCurrentDoctorId());
            stmt.setString(3, date.format(formatter));
            stmt.setString(4, time);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Termin konnte nicht gespeichert werden");
            }
            connection.commit();
        }
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) throws SQLException {
        String sql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.date = ? " +
                "ORDER BY a.time";

        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, date.format(formatter));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
            connection.commit();
            return appointments;
        }
    }

    private String getCurrentDoctorId() throws SQLException {
        String sql = "SELECT user_id FROM Doctor LIMIT 1";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String doctorId = rs.getString("user_id");
                    connection.commit();
                    return doctorId;
                }
                throw new SQLException("Kein Arzt in der Datenbank gefunden");
            }
        }
    }

    private Appointment createAppointmentFromResultSet(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getInt("appointment_id"),
                rs.getString("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("doctor_id"),
                rs.getString("date"),
                rs.getString("time"),
                rs.getInt("risk_group")
        );
    }
}