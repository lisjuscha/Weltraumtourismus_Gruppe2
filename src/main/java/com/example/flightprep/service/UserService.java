package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.User;

public class UserService {
    private final UserDAO userDAO;
    private static final Object LOCK = new Object();

    public UserService() {
        this.userDAO = new UserDAO();
    }


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