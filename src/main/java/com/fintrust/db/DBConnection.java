package com.fintrust.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.util.NotificationUtil;

/**
 * Single ton class 
 * Establishing DBConnection by hard-coded
 */
public class DBConnection {

    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/fintrust_bank";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";

    private static Connection connection = null;

    // Private constructor to prevent external instantiation
    private DBConnection() {}
    
    private static final Logger logger = LogManager.getLogger(DBConnection.class);

    /**
     * Establishing connection and returning connection object
     * @return
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

               logger.info(" Database Connected Successfully. log");
            }
        } catch (ClassNotFoundException e) {
        	NotificationUtil.showInstant("error", "Server error");
        	logger.fatal("JDBC Driver not found:", e);
        } catch (SQLException e) {
        	NotificationUtil.showInstant("error", "Server error");
        	logger.fatal("Database Connection Error:", e);
        }
        return connection;
    }
    
}


