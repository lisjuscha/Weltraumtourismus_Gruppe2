package com.example.flightprep.database;

import java.sql.*;

public class SQLiteConnection implements DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/FlightPreperation.db";

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    @Override
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    try {
                        connection.rollback();
                    } catch (SQLException e) {
                        System.err.println("Error rolling back transaction: " + e.getMessage());
                    }
                }
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}