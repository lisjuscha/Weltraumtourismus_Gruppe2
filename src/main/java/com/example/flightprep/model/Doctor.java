package com.example.flightprep.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor extends User {
    private final String firstName;
    private final String lastName;
    private final String email;

    public Doctor(String userId, String password,
                  String firstName, String lastName, String email) {
        super(userId, password, "doctor");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getter-Methoden
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
}