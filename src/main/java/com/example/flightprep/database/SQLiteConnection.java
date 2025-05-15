package com.example.flightprep.database;

import java.sql.*;

/**
 * The `SQLiteConnection` class provides an implementation of the `DatabaseConnection` interface
 * for managing connections to an SQLite database. It includes methods to establish and close
 * database connections, as well as handle transaction rollbacks when necessary.
 */
public class SQLiteConnection implements DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/FlightPreperation.db";

    /**
     * Establishes a connection to the SQLite database.
     * The connection is configured to not auto-commit transactions.
     *
     * @return A `Connection` object representing the database connection.
     * @throws SQLException If an error occurs while establishing the connection or loading the SQLite JDBC driver.
     */
    @Override
    public Connection getConnection() throws SQLException {
        try {
            // Explicitly load the SQLite JDBC driver.
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(DB_URL);
            // Disable auto-commit for manual transaction control.
            connection.setAutoCommit(false);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
    }

    /**
     * Closes the provided database connection. If the connection is not in auto-commit mode,
     * it attempts to roll back any uncommitted transactions before closing.
     *
     * @param connection The `Connection` object to be closed.
     */
    @Override
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    try {
                        // Rollback any pending transactions if auto-commit was off.
                        connection.rollback();
                    } catch (SQLException e) {
                        System.err.println("Error rolling back transaction: " + e.getMessage());
                    }
                }
                // Re-enable auto-commit before closing (good practice, though connection is being closed).
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}