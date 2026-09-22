package com.distributedexam.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database credentials and URL should ideally be in a properties file.
    // For this assignment, we set them directly. Ensure your local PostgreSQL matches.
    private static final String URL = "jdbc:postgresql://localhost:5432/dcs_exam";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
