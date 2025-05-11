package com.example.flightprep.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Customer extends User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private boolean formSubmitted;
    private boolean appointmentMade;
    private boolean fileUploaded;

    public Customer(String userId, String password,
                    String firstName, String lastName, String email,
                    boolean formSubmitted, boolean appointmentMade, boolean fileUploaded) {
        super(userId, password, "customer");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.formSubmitted = formSubmitted;
        this.appointmentMade = appointmentMade;
        this.fileUploaded = fileUploaded;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public boolean isFormSubmitted() { return formSubmitted; }
    public void setFormSubmitted(boolean formSubmitted) { this.formSubmitted = formSubmitted; }
    public boolean isAppointmentMade() { return appointmentMade; }
    public void setAppointmentMade(boolean appointmentMade) { this.appointmentMade = appointmentMade; }
    public boolean isFileUploaded() { return fileUploaded; }
    public void setFileUploaded(boolean fileUploaded) { this.fileUploaded = fileUploaded; }
}