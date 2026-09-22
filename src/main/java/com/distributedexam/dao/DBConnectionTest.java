package com.distributedexam.dao;

import java.sql.Connection;

public class DBConnectionTest {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("=================================");
            System.out.println("DATABASE CONNECTION SUCCESSFUL");
            System.out.println("=================================");

            System.out.println("Database: "
                    + connection.getCatalog());

            System.out.println("URL: "
                    + connection.getMetaData().getURL());

            System.out.println("User: "
                    + connection.getMetaData().getUserName());

        } catch (Exception e) {

            System.out.println("=================================");
            System.out.println("DATABASE CONNECTION FAILED");
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}   