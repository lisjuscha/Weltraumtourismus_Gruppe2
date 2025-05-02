package com.example.flightprep.dao;

import com.example.flightprep.model.MedicalData;
import com.example.flightprep.util.DbConnection;
import com.example.flightprep.util.SessionManager;

import java.sql.*;

public class MedicalDataDAO {
    private final Connection connection;

    public MedicalDataDAO(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        this.connection = connection;
    }

    public void save(MedicalData data) throws SQLException {
        String sql = "INSERT or REPLACE INTO medical_data (user_id, height, weight, alcohol_consumption, " +
                "smoking_status, training_status, disability_status, disability_details, heart_disease, " +
                "high_blood_pressure, irregular_heartbeat, stroke_history, asthma, lung_disease, " +
                "seizure_history, neurological_disorder, hsp_respiratory_cardio, hsp_heart_lung, " +
                "persc_med, allergies, surgery, ser_injury) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, SessionManager.getCurrentUserId());
            stmt.setString(2, data.getHeight());
            stmt.setString(3, data.getWeight());
            stmt.setString(4, data.getAlcoholConsumption());
            stmt.setString(5, data.getSmokingStatus());
            stmt.setBoolean(6, data.isTrainingStatus());
            stmt.setBoolean(7, data.isDisabilityStatus());
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
            stmt.executeUpdate();
        }
    }
}