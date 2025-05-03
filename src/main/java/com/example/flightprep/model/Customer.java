package com.example.flightprep.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer extends User {
    private String firstName;
    private String lastName;
    private String email;
    private int fromSubmitted;

    public Customer(String user_id, String password, Connection conn) {
        super(user_id, password, "customer");
        try (PreparedStatement stmt = conn.prepareStatement("SELECT first_name, last_name, email FROM Customer WHERE user_id = ?")) {
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.firstName = rs.getString("first_name");
                this.lastName = rs.getString("last_name");
                this.email = rs.getString("email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public int getFromSubmitted() {
        return this.fromSubmitted;
    }
}

