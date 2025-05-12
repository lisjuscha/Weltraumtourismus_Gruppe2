package com.example.flightprep.dao;

import com.example.flightprep.model.Customer;
import com.example.flightprep.model.Doctor;
import com.example.flightprep.model.User;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * UserDAO is a Data Access Object that provides an interface to interact with the user database.
 * It contains methods to retrieve information.
 */
public class UserDAO {

    private final Connection connection;

    // Constructor
    public UserDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
    }

    // Method to get a user by user_id
    public User getUserByUserID(String userId) {
        // SQL query to select user by user_id
        String sql = "SELECT * FROM User WHERE user_id = ?";
        // Establishing a connection to the database and preparing the statement
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            if (connection != null) {
                System.out.println("✅ Connected to the SQLite database.");
            }
            // Setting the username parameter in the SQL query
            stmt.setString(1, userId);
            // Executing the query and getting the result set
            try (ResultSet rs = stmt.executeQuery()) {
                // Checking if a result is returned
                if (rs.next()) {

                    String role = rs.getString("role");
                    String password = rs.getString("password");

                    if (role.equals("doctor")) {
                        return getDoctorByUserId(userId, password); // Werte aus DB holen
                    }
                    if (role.equals("customer")) {
                        return getCustomerByUserId(userId, password); // Werte aus DB holen
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        // If no user is found, return null
        return null;
    }
    public Customer getCustomerByUserId(String userId, String password) throws SQLException {
        String sql = "SELECT first_name, last_name, email, form_submitted, appointment_made, file_uploaded, flight_date, risk_group " +
                "FROM Customer WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            userId,
                            password,
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
        }
        return null;
    }

    public Doctor getDoctorByUserId(String userId, String password) throws SQLException {
        String sql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                            userId,
                            password,
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }



    // Method to update the form submitted status of a user
    public void updateFormSubmittedStatus(String userId, boolean status) throws SQLException {
        String sql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            // Setting the parameters for the SQL query
            stmt.setBoolean(1, status);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public boolean getFormSubmittedStatus(String userId) throws SQLException {
        String sql = "SELECT form_submitted FROM Customer WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("form_submitted") == 1;
            }
        }
    }

    public boolean getAppointmentStatus(String userId) throws SQLException {
        String sql = "SELECT appointment_made FROM Customer WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("appointment_made") == 1;
            }
        }
    }

    public boolean getUploadStatus(String userId) throws SQLException {
        String sql = "SELECT file_uploaded FROM Customer WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("file_uploaded") == 1;
            }
        }
    }

    public LocalDate getFlightDate(String userId) throws SQLException {
        String sql = "SELECT flight_date FROM Customer WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return LocalDate.parse(rs.getString("flight_date"), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }
            }
        }
        return null;
    }

    public void updateFileUploadStatus() {
        String sql = "UPDATE Customer SET file_uploaded = 1 WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, SessionManager.getCurrentUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while updating file upload status: " + e.getMessage());
        }
    }


    public void setAppointmentStatus() {
        String sql = "UPDATE Customer SET appointment_made = 1 WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, SessionManager.getCurrentUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while updating appointment status: " + e.getMessage());
        }
    }
}

