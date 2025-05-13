package com.example.flightprep.model;

/**
 * The `Customer` class represents a customer in the Flight Preparation application.
 * It extends the `User` class and includes additional details specific to customers,
 * such as personal information, flight details, and status flags.
 */

public class Customer extends User {
    private String firstName;
    private String lastName;
    private String email;
    private boolean formSubmitted;
    private boolean appointmentMade;
    private boolean fileUploaded;
    private String flightDate;
    private int riskGroup;

    /**
     * Constructs a `Customer` object with the specified details.
     *
     * @param userId          The unique ID of the customer.
     * @param password        The password of the customer.
     * @param firstName       The first name of the customer.
     * @param lastName        The last name of the customer.
     * @param email           The email address of the customer.
     * @param formSubmitted   Whether the customer has submitted the form.
     * @param appointmentMade Whether the customer has made an appointment.
     * @param fileUploaded    Whether the customer has uploaded a file.
     * @param flightDate      The flight date of the customer.
     * @param riskGroup       The risk group of the customer.
     */

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

    /**
     * Gets the first name of the customer.
     *
     * @return The customer's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name of the customer.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of the customer.
     *
     * @return The customer's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name of the customer.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email address of the customer.
     *
     * @return The customer's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the customer.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if the customer has submitted the form.
     *
     * @return `true` if the form is submitted, otherwise `false`.
     */
    public boolean isFormSubmitted() {
        return formSubmitted;
    }

    /**
     * Sets the form submission status of the customer.
     *
     * @param formSubmitted The form submission status to set.
     */
    public void setFormSubmitted(boolean formSubmitted) {
        this.formSubmitted = formSubmitted;
    }

    /**
     * Checks if the customer has made an appointment.
     *
     * @return `true` if an appointment is made, otherwise `false`.
     */
    public boolean isAppointmentMade() {
        return appointmentMade;
    }

    /**
     * Sets the appointment status of the customer.
     *
     * @param appointmentMade The appointment status to set.
     */
    public void setAppointmentMade(boolean appointmentMade) {
        this.appointmentMade = appointmentMade;
    }

    /**
     * Checks if the customer has uploaded a file.
     *
     * @return `true` if a file is uploaded, otherwise `false`.
     */
    public boolean isFileUploaded() {
        return fileUploaded;
    }

    /**
     * Sets the file upload status of the customer.
     *
     * @param fileUploaded The file upload status to set.
     */
    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }

    /**
     * Gets the risk group of the customer.
     *
     * @return The customer's risk group.
     */
    public int getRiskGroup() {
        return this.riskGroup;
    }

    /**
     * Gets the flight date of the customer.
     *
     * @return The customer's flight date.
     */
    public String getFlightDate() {
        return flightDate;
    }

    /**
     * Sets the flight date of the customer.
     *
     * @param flightDate The flight date to set.
     */
    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    /**
     * Sets the risk group of the customer.
     *
     * @param riskGroup The risk group to set.
     */
    public void setRiskGroup(int riskGroup) {
        this.riskGroup = riskGroup;
    }
}
