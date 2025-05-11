package com.example.flightprep.database;

public class DatabaseFactory {
    private static final DatabaseConnection instance = new SQLiteConnection();

    public static DatabaseConnection getDatabase() {
        return instance;
    }
}
