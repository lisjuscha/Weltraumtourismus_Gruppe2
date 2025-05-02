package com.example.flightprep.util;

import java.sql.*;

public class DbConnection {
    private static final String DB_URL = "jdbc:sqlite:data/FlightPreperation.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(DB_URL);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute("PRAGMA busy_timeout = 30000");
                stmt.execute("PRAGMA journal_mode = WAL");
            }

            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    public static void closeConnection(Connection connection) {
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