package com.example.flightprep.model;

import java.time.LocalDate;

public class Customer extends User {
    private String firstName;
    private String lastName;
    private String email;
    private boolean formSubmitted;
    private boolean appointmentMade;
    private boolean fileUploaded;
    private String flightDate;
    private int riskGroup;

    public Customer(String userId, String password,
                   String firstName, String lastName, String email,
                   boolean formSubmitted, boolean appointmentMade, boolean fileUploaded,
                   String flightDate, int riskGroup) {
        super(userId, password, "customer");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.formSubmitted = formSubmitted;
        this.appointmentMade = appointmentMade;
        this.fileUploaded = fileUploaded;
        this.flightDate = flightDate;
        this.riskGroup = riskGroup;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isFormSubmitted() {
        return formSubmitted;
    }

    public void setFormSubmitted(boolean formSubmitted) {
        this.formSubmitted = formSubmitted;
    }

    public boolean isAppointmentMade() {
        return appointmentMade;
    }

    public void setAppointmentMade(boolean appointmentMade) {
        this.appointmentMade = appointmentMade;
    }

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    public int getRiskGroup() {
        return this.riskGroup;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }
}
