package com.example.flightprep.service;

import com.example.flightprep.dao.CustomerDao;
import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.Customer;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private static CustomerService instance;
    private final CustomerDao customerDao;
    private final DatabaseConnection databaseConnection;
    private static final Object LOCK = new Object();

    private CustomerService() {
        this.customerDao = new CustomerDao();
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public static CustomerService getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new CustomerService();
                }
            }
        }
        return instance;
    }

    public List<Customer> getPatientsWithUploadedFiles() {
        return customerDao.findAllWithUploadedFiles();
    }

    public Customer getCustomerStatus(String userId) {
        synchronized (LOCK) {
            try (Connection conn = databaseConnection.getConnection()) {
                UserDAO userDAO = new UserDAO(conn);
                Customer customer = userDAO.getCustomerByUserId(userId, null);

                if (customer == null) {
                    throw new RuntimeException("Customer not found");
                }

                conn.commit();
                return customer;
            } catch (SQLException e) {
                throw new RuntimeException("Error getting customer status", e);
            }
        }
    }

    public void submitMedicalData(MedicalData data) throws SQLException {
        synchronized (LOCK) {
            try (Connection conn = databaseConnection.getConnection()) {
                MedicalDataDAO medicalDao = new MedicalDataDAO(conn);
                medicalDao.save(data);

                int riskGroup = RiskClassifierAI.classifyRisk(data);
                updateCustomerRiskGroup(conn, riskGroup);

                UserDAO userDao = new UserDAO(conn);
                userDao.updateFormSubmittedStatus(SessionManager.getCurrentUserId(), true);

                conn.commit();
            }
        }
    }

    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        synchronized (LOCK) {
            try (Connection conn = databaseConnection.getConnection()) {
                String sql = "UPDATE Customer SET declaration = ?, comment = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setBoolean(1, isApproved);
                    stmt.setString(2, comment);
                    stmt.setString(3, userId);

                    int affectedRows = stmt.executeUpdate();
                    conn.commit();

                    if (affectedRows == 0) {
                        throw new SQLException("Keine Daten aktualisiert");
                    }
                }
            }
        }
    }

    private void updateCustomerRiskGroup(Connection conn, int riskGroup) throws SQLException {
        String sql = "UPDATE customer SET risk_group = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riskGroup);
            pstmt.setString(2, SessionManager.getCurrentUser().getUserId());
            pstmt.executeUpdate();
        }
    }
    public void updateFileUploadStatus(String userId) throws SQLException {
        synchronized (LOCK) {
            try (Connection conn = databaseConnection.getConnection()) {
                String sql = "UPDATE customer SET file_uploaded = true WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, userId);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Kunde konnte nicht aktualisiert werden");
                    }

                    conn.commit();
                }
            }
        }
    }
}