package com.example.flightprep.util;
import java.sql.*;
public class DbConnection {

    public static Connection getConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data/FlightPreperation.db");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}