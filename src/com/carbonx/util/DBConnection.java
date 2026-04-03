package com.carbonx.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/carbonx";
    private static final String USER     = "root";
    private static final String PASSWORD = "";   // your XAMPP password

    private static Connection connection = null;
    private static boolean    printed    = false; // print connected message only once

    private DBConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                if (!printed) {
                    System.out.println("Database connected successfully!");
                    printed = true;
                }
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                printed = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}