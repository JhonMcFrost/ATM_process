package com.example;

import java.sql.*;

// Configuration Constants
public class DBConnection {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/vaultxbank";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    //Method to connect to database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}