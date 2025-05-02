package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.User;
import com.example.flightprep.util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private static final Object LOCK = new Object();

    public User authenticateUser(String username, String password) {
        synchronized (LOCK) {
            try (Connection conn = DbConnection.getConnection()) {
                UserDAO userDAO = new UserDAO(conn);
                User user = userDAO.getUserByUserID(username);
                if (user != null && password.equals(user.getPassword())) {
                    return user;
                }
                return null;
            } catch (SQLException e) {
                System.out.println("❌ Connection failed: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Authentication failed", e);
            }
        }
    }
}