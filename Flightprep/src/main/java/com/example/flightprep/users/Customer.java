package com.example.flightprep.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer extends User{
    protected String first_name;
    protected String last_name;
    protected String email;
    protected int from_submitted;

    public Customer(String user_id, String password, Connection conn) {
        super(user_id, password, "customer");
        this.user_id = user_id;
        try(PreparedStatement stmt = conn.prepareStatement("SELECT first_name, last_name, email FROM Customer WHERE user_id = ?")) {
            stmt.setString(1, user_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                this.first_name = rs.getString("first_name");
                this.last_name = rs.getString("last_name");
                this.email = rs.getString("email");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
