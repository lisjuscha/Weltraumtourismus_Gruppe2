package com.example.flightprep.dao;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;
import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.SessionManager;

import java.sql.*;


public class MedicalDataDAO {
    private final DatabaseConnection databaseConnection;

    public MedicalDataDAO() {
        this.databaseConnection = DatabaseFactory.getDatabase();
    }

    public void save(MedicalData data) throws SQLException {
        String sql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, SessionManager.getCurrentUserId());
            stmt.setString(2, data.getHeight());
            stmt.setString(3, data.getWeight());
            stmt.setString(4, data.getAlcoholConsumption());
            stmt.setString(5, data.getSmokingStatus());
            stmt.setBoolean(6, data.isTrainingStatus());
            stmt.setBoolean(7, data.getDisabilityStatus());
            stmt.setString(8, data.getDisabilityDetails());
            stmt.setBoolean(9, data.isHeartDisease());
            stmt.setBoolean(10, data.isHighBloodPressure());
            stmt.setBoolean(11, data.isIrregularHeartbeat());
            stmt.setBoolean(12, data.isStrokeHistory());
            stmt.setBoolean(13, data.isAsthma());
            stmt.setBoolean(14, data.isLungDisease());
            stmt.setBoolean(15, data.isSeizureHistory());
            stmt.setBoolean(16, data.isNeurologicalDisorder());
            stmt.setBoolean(17, data.isHsp_respiratory_cardio());
            stmt.setBoolean(18, data.isHsp_heart_lung());
            stmt.setBoolean(19, data.isPersc_med());
            stmt.setBoolean(20, data.isAllergies());
            stmt.setBoolean(21, data.isSurgery());
            stmt.setBoolean(22, data.isSer_injury());

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Speichern der medizinischen Daten fehlgeschlagen");
            }
            connection.commit();
        }
    }

    public MedicalData getDataByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM medical_data WHERE user_id = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MedicalData data = new MedicalData();
                    mapResultSetToMedicalData(rs, data);
                    connection.commit();
                    return data;
                }
            }
        }
        return null;
    }

    private void mapResultSetToMedicalData(ResultSet rs, MedicalData data) throws SQLException {
        data.setHeight(rs.getString("height"));
        data.setWeight(rs.getString("weight"));
        data.setAlcoholConsumption(rs.getString("alcohol_consumption"));
        data.setSmokingStatus(rs.getString("smoking_status"));
        data.setTrainingStatus(rs.getBoolean("training_status"));
        data.setDisabilityStatus(rs.getBoolean("disability_status"));
        data.setDisabilityDetails(rs.getString("disability_details"));
        data.setHeartDisease(rs.getBoolean("heart_disease"));
        data.setHighBloodPressure(rs.getBoolean("high_blood_pressure"));
        data.setIrregularHeartbeat(rs.getBoolean("irregular_heartbeat"));
        data.setStrokeHistory(rs.getBoolean("stroke_history"));
        data.setAsthma(rs.getBoolean("asthma"));
        data.setLungDisease(rs.getBoolean("lung_disease"));
        data.setSeizureHistory(rs.getBoolean("seizure_history"));
        data.setNeurologicalDisorder(rs.getBoolean("neurological_disorder"));
        data.setHsp_respiratory_cardio(rs.getBoolean("hsp_respiratory_cardio"));
        data.setHsp_heart_lung(rs.getBoolean("hsp_heart_lung"));
        data.setPersc_med(rs.getBoolean("persc_med"));
        data.setAllergies(rs.getBoolean("allergies"));
        data.setSurgery(rs.getBoolean("surgery"));
        data.setSer_injury(rs.getBoolean("ser_injury"));
    }
}