package com.example.flightprep.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The `DatabaseConnection` interface defines the contract for managing database connections.
 * It provides methods to establish and close connections to a database.
 */
public interface DatabaseConnection {

    /**
     * Establishes a connection to the database.
     *
     * @return A `Connection` object representing the database connection.
     * @throws SQLException If an error occurs while establishing the connection.
     */
    Connection getConnection() throws SQLException;

    /**
     * Closes the provided database connection.
     *
     * @param connection The `Connection` object to be closed.
     */
    void closeConnection(Connection connection);
}
