package com.example.flightprep.dao;

import com.example.flightprep.users.Customer;
import com.example.flightprep.users.Doctor;
import com.example.flightprep.users.User;
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
    // Method to get a user by user_id
    public User getUserByUserID(String user_id) {
        // SQL query to select user by user_id
        String sql = "SELECT * FROM User WHERE user_id = ?";
        // Establishing a connection to the database and preparing the statement
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn != null) {
                System.out.println("✅ Connected to the SQLite database.");
            }
            // Setting the username parameter in the SQL query
            stmt.setString(1, user_id);
            // Executing the query and getting the result set
            ResultSet rs = stmt.executeQuery();
            // Checking if a result is returned
            if (rs.next()) {
                String role = rs.getString("role");
                String u_id = rs.getString("user_id");
                String password = rs.getString("password");

                if (role.equals("doctor")) {
                    return new Doctor(user_id, password, conn); // Werte aus DB holen
                } else {
                    return new Customer(user_id, password, conn); // Werte aus DB holen
                }
            }
            // If no user is found, return null

        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
