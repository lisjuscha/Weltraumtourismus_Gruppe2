package com.example.flightprep.database;

/**
 * The `DatabaseFactory` class provides a singleton instance of a `DatabaseConnection`.
 * It is used to retrieve the database connection implementation for the application.
 */
public class DatabaseFactory {

    private static final DatabaseConnection instance = new SQLiteConnection();

    /**
     * Retrieves the singleton instance of the `DatabaseConnection`.
     *
     * @return The `DatabaseConnection` instance.
     */
    public static DatabaseConnection getDatabase() {
        return instance;
    }
}
