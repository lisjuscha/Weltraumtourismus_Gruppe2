package com.example.flightprep.service;

import com.example.flightprep.database.DatabaseConnection;
import com.example.flightprep.database.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeclarationService {
    private final DatabaseConnection dbConnection;

    public DeclarationService() {
        this.dbConnection = DatabaseFactory.getDatabase();
    }

    public void saveDeclaration(String userId, boolean isApproved, String comment) throws SQLException {
        String sql = "UPDATE Customer SET declaration = ?, comment = ? WHERE user_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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