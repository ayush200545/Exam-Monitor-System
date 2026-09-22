package com.distributedexam.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String URL = getEnv("DB_URL", "jdbc:postgresql://localhost:5432/dcs_exam");
    private static final String USER = getEnv("DB_USER", "postgres");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "password");

    private DBConnection() {
        // Utility class - prevent object creation
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }
}
