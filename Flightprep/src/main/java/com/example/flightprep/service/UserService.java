package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.users.User;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User authenticateUser(String username, String password) {
        User user = userDAO.getUserByUserID(username);
        if (user != null && password.equals(user.getPassword())) { // dummy-check
            return user;
        }
        return null;
    }
}
