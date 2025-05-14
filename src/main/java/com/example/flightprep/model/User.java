package com.example.flightprep.model;

/**
 * The `User` class represents a generic user in the Flight Preparation application.
 * It serves as a base class for specific user types such as `Customer` and `Doctor`.
 * The class includes common attributes such as user ID, password, and role.
 */
public class User {
    private String userId;
    private String password;
    private String role;

    /**
     * Constructs a `User` object with the specified details.
     *
     * @param user_id  The unique ID of the user.
     * @param password The password of the user.
     * @param role     The role of the user (e.g., "customer", "doctor").
     */
    public User(String user_id, String password, String role) {
        this.userId = user_id;
        this.password = password;
        this.role = role;
    }


    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The new user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the password of the user.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the role of the user.
     *
     * @return The user's role.
     */
    public String getRole() {
        return this.role;
    }

}
