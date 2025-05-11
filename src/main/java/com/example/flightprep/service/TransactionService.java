package com.example.flightprep.service;

import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionService {
    private final DatabaseConnection databaseConnection;

    public TransactionService() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public void submitMedicalSurvey(ActionEvent actionEvent, MedicalData data) throws SQLException {
        Connection conn = null;
        try {
            conn = databaseConnection.getConnection();

            // Save the medical data
            MedicalDataDAO medicalDao = new MedicalDataDAO(conn);
            medicalDao.save(data);

            int riskGroup = RiskClassifierAI.classifyRisk(data);
            // Update the risk group in the database
            updateCustomerRiskGroup(conn, riskGroup);

            // Update the user's form submitted status
            UserDAO userDao = new UserDAO(conn);
            userDao.updateFormSubmittedStatus(SessionManager.getCurrentUserId(), true);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            DbConnection.closeConnection(conn);
            try {
                SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", actionEvent);

            } catch (Exception e) {
                System.out.println("Error switching to survey screen: " + e.getMessage());
            }
        }
    }
    private static void updateCustomerRiskGroup(Connection conn, int riskGroup) throws SQLException {
        String sql = "UPDATE customer SET risk_group = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, riskGroup);
            pstmt.setString(2, SessionManager.getCurrentUser().getId());
            pstmt.executeUpdate();
        }
    }
}
