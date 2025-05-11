package com.example.flightprep.service;

import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerStatusService  {
    private final DatabaseConnection databaseConnection;
    private static final Object LOCK = new Object();

    public CustomerStatusService() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }


    public Customer getCustomerStatus(String userId) {
        synchronized (LOCK) {
            Connection conn = null;
            try {
                conn = databaseConnection.getConnection();
                UserDAO userDAO = new UserDAO(conn);
                Customer customer = userDAO.getCustomerByUserId(userId, null);

                if (customer == null) {
                    throw new RuntimeException("Customer not found");
                }

                conn.commit();
                return customer;
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Rollback failed", ex);
                    }
                }
                throw new RuntimeException("Error getting customer status", e);
            } finally {
                databaseConnection.closeConnection(conn);
            }
        }
    }
}
