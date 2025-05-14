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

/**
 * The `AppointmentDAO` class provides data access methods for managing appointments
 * in the Flight Preparation application. It interacts with the database to perform
 * CRUD operations related to appointments.
 */
public class AppointmentDAO {
    private static AppointmentDAO instance;
    private static final Object LOCK = new Object();
    private final DatabaseConnection databaseConnection;
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Constructs an `AppointmentDAO` instance and initializes the database connection.
     */
    public AppointmentDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public static AppointmentDAO getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new AppointmentDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Checks if a specific time slot on a given date is already booked.
     *
     * @param date The date to check.
     * @param time The time slot to check.
     * @return `true` if the slot is booked, `false` otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isSlotBooked(LocalDate date, String time) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE date = ? AND time = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, date.format(DATE_FORMATTER));
            stmt.setString(2, time);
            try (ResultSet rs = stmt.executeQuery()) {                   
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Books an appointment for a customer at a specific date and time.
     *
     * @param customerId The ID of the customer booking the appointment.
     * @param date       The date of the appointment.
     * @param time       The time of the appointment.
     * @throws SQLException If a database access error occurs or the appointment cannot be saved.
     */
    public void bookAppointment(String customerId, LocalDate date, String time) throws SQLException {
        Appointment existingAppointment = getAppointmentByCustomerId(customerId);
        String sql;
        if (existingAppointment != null) {
            // Update existing appointment
            sql = "UPDATE appointments SET doctor_id = ?, date = ?, time = ? WHERE customer_id = ?";
        } else {
            // Insert new appointment
            sql = "INSERT INTO appointments (customer_id, doctor_id, date, time) VALUES (?, ?, ?, ?)";
        }

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (existingAppointment != null) {
                stmt.setString(1, getCurrentDoctorId());
                stmt.setString(2, date.format(DATE_FORMATTER));
                stmt.setString(3, time);
                stmt.setString(4, customerId);
            } else {
                stmt.setString(1, customerId);
                stmt.setString(2, getCurrentDoctorId());
                stmt.setString(3, date.format(DATE_FORMATTER));
                stmt.setString(4, time);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Appointment could not be saved (update or insert failed).");
            }
            connection.commit();
        }
    }

    /**
     * Retrieves a list of appointments for a specific date, including customer details.
     * Results are ordered by time.
     *
     * @param date The date for which to retrieve appointments.
     * @return A list of `Appointment` objects for the specified date.
     * @throws SQLException If a database access error occurs.
     */
    public List<Appointment> getAppointmentsByDate(LocalDate date) throws SQLException {
        String sql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                "FROM appointments a " +
                "JOIN Customer c ON a.customer_id = c.user_id " +
                "WHERE a.date = ? " +
                "ORDER BY a.time";

        List<Appointment> appointments = new ArrayList<>();
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, date.format(DATE_FORMATTER));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(createAppointmentFromResultSet(rs));
                }
            }
            return appointments;
        }
    }

    /**
     * Retrieves the appointment for a specific customer.
     * Assumes a customer has at most one relevant appointment.
     *
     * @param customerId The ID of the customer.
     * @return An `Appointment` object if found, otherwise `null`.
     * @throws SQLException If a database access error occurs.
     */
    public Appointment getAppointmentByCustomerId(String customerId) throws SQLException {
        String sql = "SELECT a.*, c.first_name, c.last_name, c.risk_group " +
                     "FROM appointments a " +
                     "JOIN Customer c ON a.customer_id = c.user_id " +
                     "WHERE a.customer_id = ? LIMIT 1"; 

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createAppointmentFromResultSet(rs);
                }
            }
        }
        return null; // No appointment found
    }

    /**
     * Retrieves the ID of the current doctor from the database.
     *
     * @return The ID of a doctor.
     * @throws SQLException If a database access error occurs or no doctor is found.
     */
    private String getCurrentDoctorId() throws SQLException {
        String sql = "SELECT user_id FROM Doctor LIMIT 1";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("user_id");
                }
                throw new SQLException("No doctor found in the database. Cannot book appointment.");
            }
        }
    }

    /**
     * Creates an `Appointment` object from a `ResultSet`.
     *
     * @param rs The `ResultSet` containing appointment data.
     * @return An `Appointment` object.
     * @throws SQLException If a database access error occurs.
     */
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