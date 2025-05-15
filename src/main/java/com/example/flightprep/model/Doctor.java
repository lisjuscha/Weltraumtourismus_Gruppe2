package com.example.flightprep.model;

/**
 * The `Doctor` class represents a doctor in the Flight Preparation application.
 * It extends the `User` class and includes additional details specific to doctors,
 * such as their first name, last name, and email address.
 */

public class Doctor extends User {
    private final String firstName;
    private final String lastName;
    private final String email;

    /**
     * Constructs a `Doctor` object with the specified details.
     *
     * @param userId    The unique ID of the doctor.
     * @param password  The password of the doctor.
     * @param firstName The first name of the doctor.
     * @param lastName  The last name of the doctor.
     * @param email     The email address of the doctor.
     */

    public Doctor(String userId, String password,
                  String firstName, String lastName, String email) {
        super(userId, password, "doctor");
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Gets the first name of the doctor.
     *
     * @return The doctor's first name.
     */
    public String getFirstName() { return firstName; }

    /**
     * Gets the last name of the doctor.
     *
     * @return The doctor's last name.
     */
    public String getLastName() { return lastName; }

    /**
     * Gets the email address of the doctor.
     *
     * @return The doctor's email address.
     */
    public String getEmail() { return email; }
}