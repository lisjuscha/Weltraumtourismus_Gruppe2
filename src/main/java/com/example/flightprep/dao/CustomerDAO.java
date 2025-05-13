package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * The `CustomerDAO` class provides data access methods for managing customer-related
 * operations in the Flight Preparation application. It interacts with the database
 * to perform CRUD operations for customer data.
 */
public class CustomerDAO {
    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a `CustomerDAO` instance and initializes the database connection.
     */
    public CustomerDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    /**
     * Retrieves a list of customers who have uploaded files, ordered by their flight date.
     *
     * @return A list of `Customer` objects with uploaded files.
     * @throws SQLException If a database access error occurs.
     */
    public List<Customer> findAllWithUploadedFiles() throws SQLException {
        String sql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                "FROM User u " +
                "JOIN Customer c ON u.user_id = c.user_id AND c.declaration IS NULL " +
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

    /**
     * Updates the declaration status and comment for a specific customer.
     *
     * @param userId    The ID of the customer.
     * @param isApproved The approval status of the declaration.
     * @param comment   The comment associated with the declaration.
     * @throws SQLException If a database access error occurs.
     */
    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        String sql = "UPDATE Customer SET declaration = ?, declaration_comment = ? WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, isApproved ? 1 : 0);
            stmt.setString(2, comment);
            stmt.setString(3, userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No data updated");
            }
            connection.commit();
        }
    }

    /**
     * Updates the risk group of a specific customer.
     *
     * @param userId    The ID of the customer.
     * @param riskGroup The risk group to set.
     * @throws SQLException If a database access error occurs.
     */

    public void updateCustomerRiskGroup(String userId, int riskGroup) throws SQLException {
        String sql = "UPDATE customer SET risk_group = ? WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, riskGroup);
            stmt.setString(2, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Risk group could not be updated");
            }
            connection.commit();
        }
    }

    /**
     * Updates the file upload status of a specific customer to true.
     *
     * @param userId The ID of the customer.
     * @throws SQLException If a database access error occurs.
     */
    public void updateFileUploadStatus(String userId) throws SQLException {
        String sql = "UPDATE customer SET file_uploaded = true WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Customer could not be updated");
            }
            connection.commit();
        }
    }

    /**
     * Updates the appointment status of a specific customer to true.
     *
     * @param userId The ID of the customer.
     * @throws SQLException If a database access error occurs.
     */
    public void updateAppointmentStatus(String userId) throws SQLException {
        String sql = "UPDATE customer SET has_appointment = TRUE WHERE user_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Appointment status could not be updated");
            }
            connection.commit();
        }
    }

    /**
     * Retrieves the appointment status of a specific customer.
     *
     * @param userId The ID of the customer.
     * @return `true` if the appointment is made, `false` otherwise.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Updates the form submission status of a specific customer.
     *
     * @param userId The ID of the customer.
     * @param status The form submission status to set.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves the form submission status of a specific customer.
     *
     * @param userId The ID of the customer.
     * @return `true` if the form is submitted, `false` otherwise.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Retrieves the flight date of a specific customer.
     *
     * @param userId The ID of the customer.
     * @return The flight date as a `LocalDate`, or `null` if not found.
     * @throws SQLException If a database access error occurs.
     */
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

    /**
     * Maps a `ResultSet` to a `Customer` object.
     *
     * @param rs The `ResultSet` containing customer data.
     * @return A `Customer` object.
     * @throws SQLException If a database access error occurs.
     */
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