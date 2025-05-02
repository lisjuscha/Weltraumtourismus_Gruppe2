package com.example.flightprep.dao;

import com.example.flightprep.model.Customer;
import com.example.flightprep.model.Doctor;
import com.example.flightprep.model.User;
import com.example.flightprep.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO is a Data Access Object that provides an interface to interact with the user database.
 * It contains methods to retrieve information.
 */
public class UserDAO {

    private final Connection connection;
    // Constructor
    public UserDAO (Connection connection) {
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
            try (ResultSet rs = stmt.executeQuery()){
                // Checking if a result is returned
                if (rs.next()) {

                    String role = rs.getString("role");
                    String password = rs.getString("password");

                    if (role.equals("doctor")) {
                        return new Doctor(userId, password, connection); // Werte aus DB holen
                    }
                    if (role.equals("customer")) {
                        return new Customer(userId, password, connection); // Werte aus DB holen
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
    // Method to update the form submitted status of a user
    public void updateFormSubmittedStatus(String userId, boolean status) throws SQLException {
        String sql = "UPDATE Customer SET form_submitted = ? WHERE user_id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)){
            // Setting the parameters for the SQL query
            stmt.setBoolean(1, status);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
        }
}
