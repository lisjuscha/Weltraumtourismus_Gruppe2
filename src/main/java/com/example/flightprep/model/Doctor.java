package com.example.flightprep.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor extends User {
    protected String first_name;
    protected String last_name;
    protected String email;

    // DAO
    public Doctor(String user_id, String password, Connection conn) {
        super(user_id, password, "doctor");
        try(PreparedStatement stmt = conn.prepareStatement("SELECT first_name, last_name, email FROM Doctor WHERE user_id = ?");) {
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                this.first_name = rs.getString("first_name");
                this.last_name = rs.getString("last_name");
                this.email = rs.getString("email");
            }
            else {
                System.out.println("❌ No doctor found with user_id: " + user_id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}