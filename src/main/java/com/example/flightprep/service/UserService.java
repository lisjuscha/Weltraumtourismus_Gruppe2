package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.User;

/**
 * The `UserService` class provides business logic for managing user-related operations
 * in the Flight Preparation application. It handles tasks such as user authentication
 * by verifying credentials against the database.
 */
public class UserService {
    private final UserDAO userDAO;
    private static final Object LOCK = new Object();

    /**
     * Constructs a new `UserService` instance.
     * Initializes the `UserDAO` for accessing user data from the database.
     */
    public UserService() {
        this.userDAO = new UserDAO();
    }


    /**
     * Authenticates a user by verifying their username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The `User` object if authentication is successful, otherwise `null`.
     * @throws RuntimeException If an error occurs during authentication.
     */
    public User authenticateUser(String username, String password) {
        synchronized (LOCK) {
            try {
                User user = userDAO.getUserByUserID(username);

                if (user != null && password.equals(user.getPassword())) {
                    return user;
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException("Authentication failed", e);
            }
        }
    }
}