package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {
    private final DatabaseConnection databaseConnection;

    public CustomerDao() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public List<Customer> findAllWithUploadedFiles() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.password, c.first_name, c.last_name, c.email, " +
                    "c.form_submitted, c.appointment_made, c.file_uploaded, c.flight_date, c.risk_group " +
                    "FROM User u " +
                    "JOIN Customer c ON u.user_id = c.user_id " +
                    "WHERE c.file_uploaded = 1 " +
                    "ORDER BY c.flight_date ASC";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Customer customer = new Customer(
                    resultSet.getString("user_id"),
                    resultSet.getString("password"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getBoolean("form_submitted"),
                    resultSet.getBoolean("appointment_made"),
                    resultSet.getBoolean("file_uploaded"),
                    resultSet.getString("flight_date"),
                    resultSet.getInt("risk_group")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
