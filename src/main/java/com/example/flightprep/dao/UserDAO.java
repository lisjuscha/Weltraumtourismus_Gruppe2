package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;
import com.example.flightprep.model.Doctor;
import com.example.flightprep.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The `UserDAO` class provides data access methods for managing user-related operations
 * in the Flight Preparation application. It interacts with the database to retrieve
 * user information based on their role (e.g., customer or doctor).
 */
public class UserDAO {
    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a `UserDAO` instance and initializes the database connection.
     */
    public UserDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    /**
     * Retrieves a user by their user ID. Depending on the user's role, it returns
     * either a `Customer` or `Doctor` object.
     *
     * @param userId The ID of the user to retrieve.
     * @return A `User` object representing the user, or `null` if not found.
     * @throws RuntimeException If a database access error occurs.
     */
    public User getUserByUserID(String userId) {
        String sql = "SELECT * FROM User WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    String password = rs.getString("password");

                    // Based on the role, fetch the specific user type (Doctor or Customer).
                    if (role.equals("doctor")) {
                        return getDoctorByUserId(userId, password);
                    }
                    if (role.equals("customer")) {
                        return getCustomerByUserId(userId, password);
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user by ID", e);
        }
        return null;
    }

    /**
     * Retrieves a customer by their user ID and password.
     *
     * @param userId   The ID of the customer to retrieve.
     * @param password The password of the customer.
     * @return A `Customer` object representing the customer, or `null` if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Customer getCustomerByUserId(String userId, String password) throws SQLException {
        String sql = "SELECT first_name, last_name, email, form_submitted, appointment_made, " +
                "file_uploaded, flight_date, risk_group FROM Customer WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer(
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
                    connection.commit();
                    return customer;
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a doctor by their user ID and password.
     *
     * @param userId   The ID of the doctor to retrieve.
     * @param password The password of the doctor.
     * @return A `Doctor` object representing the doctor, or `null` if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Doctor getDoctorByUserId(String userId, String password) throws SQLException {
        String sql = "SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Doctor doctor = new Doctor(
                            userId,
                            password,
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    );
                    connection.commit();
                    return doctor;
                }
            }
        }
        return null;
    }
}