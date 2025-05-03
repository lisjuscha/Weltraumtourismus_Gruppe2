package com.example.flightprep.service;

import com.example.flightprep.dao.MedicalDataDAO;
import com.example.flightprep.dao.UserDAO;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SceneSwitcher;
import com.example.flightprep.util.SessionManager;
import javafx.event.ActionEvent;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionService {

    public static void submitMedicalSurvey(ActionEvent actionEvent, MedicalData data) throws SQLException {
        Connection conn = null;
        try {
            conn = DbConnection.getConnection();

            // Save the medical data
            MedicalDataDAO medicalDao = new MedicalDataDAO(conn);
            medicalDao.save(data);

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
                SceneSwitcher.switchScene("/com/example/flightprep/CustomerScreens/CustomerPrep1.fxml", actionEvent, true);

            } catch (Exception e) {
                System.out.println("Error switching to survey screen: " + e.getMessage());
            }
        }
    }
}