package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final DatabaseConnection databaseConnection;

    public CustomerDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public List<Customer> findAllWithUploadedFiles() throws SQLException {
        String sql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                "FROM User u " +
                "JOIN Customer c ON u.user_id = c.user_id " +
                "WHERE c.file_uploaded = 1 " +
                "ORDER BY c.flight_date ASC";

        List<Customer> customers = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }
            connection.commit();
        }
        return customers;
    }

    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        String sql = "UPDATE Customer SET declaration = ?, comment = ? WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBoolean(1, isApproved);
            stmt.setString(2, comment);
            stmt.setString(3, userId);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Keine Daten aktualisiert");
            }
            connection.commit();
        }
    }

    public void updateCustomerRiskGroup(String userId, int riskGroup) throws SQLException {
        String sql = "UPDATE customer SET risk_group = ? WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, riskGroup);
            stmt.setString(2, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Risikogruppe konnte nicht aktualisiert werden");
            }
            connection.commit();
        }
    }

    public void updateFileUploadStatus(String userId) throws SQLException {
        String sql = "UPDATE customer SET file_uploaded = true WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Kunde konnte nicht aktualisiert werden");
            }
            connection.commit();
        }
    }
    public void updateAppointmentStatus(String userId) throws SQLException {
        String sql = "UPDATE customer SET appointment_made = true WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Terminstatus konnte nicht aktualisiert werden");
            }
            connection.commit();
        }
    }

    public boolean getAppointmentStatus(String userId) throws SQLException {
        String sql = "SELECT appointment_made FROM customer WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean status = rs.next() && rs.getBoolean("appointment_made");
                connection.commit();
                return status;
            }
        }
    }

    public void updateFormSubmittedStatus(String userId, boolean status) throws SQLException {
        String sql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBoolean(1, status);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            connection.commit();
        }
    }

    public boolean getFormSubmittedStatus(String userId) throws SQLException {
        String sql = "SELECT form_submitted FROM Customer WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean status = rs.next() && rs.getBoolean("form_submitted");
                connection.commit();
                return status;
            }
        }
    }

    public LocalDate getFlightDate(String userId) throws SQLException {
        String sql = "SELECT flight_date FROM Customer WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate date = LocalDate.parse(
                            rs.getString("flight_date"),
                            DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    );
                    connection.commit();
                    return date;
                }
            }
        }
        return null;
    }


    private Customer mapCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("user_id"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getBoolean("form_submitted"),
                rs.getBoolean("appointment_made"),
                rs.getBoolean("file_uploaded"),
                rs.getString("flight_date"),
                rs.getInt("risk_group")
        );
    }
}