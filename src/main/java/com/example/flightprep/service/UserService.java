package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.User;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private final DatabaseConnection databaseConnection;
    private static final Object LOCK = new Object();

    public UserService() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public User authenticateUser(String username, String password) {
        synchronized (LOCK) {
            Connection connection = null;
            try {
                connection = databaseConnection.getConnection();
                UserDAO userDAO = new UserDAO(connection);
                User user = userDAO.getUserByUserID(username);

                if (user != null && password.equals(user.getPassword())) {
                    connection.commit();
                    return user;
                }
                return null;
            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException rollbackEx) {
                        throw new RuntimeException("Rollback failed", rollbackEx);
                    }
                }
                throw new RuntimeException("Authentication failed", e);
            } finally {
                if (connection != null) {
                    databaseConnection.closeConnection(connection);
                }
            }
        }
    }
}