package com.fintrust.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zhtml.Main;

import org.zkoss.zk.ui.util.Clients;

import com.fintrust.util.NotificationUtil;


public class DBConnection {

    // Database configuration constants
    private static final String URL = "jdbc:mysql://localhost:3306/fintrust_bank";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";
    private static Connection connection = null;

    // Private constructor to prevent external instantiation
    private DBConnection() {}
    
    private static final Logger logger = LogManager.getLogger(DBConnection.class);

    // Public method to provide global access point
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                Statement stmt = connection.createStatement();                

                System.out.println("✅ Database Connected Successfully");     
                logger.info("✅ Database Connected Successfully. log");
            }
        } catch (ClassNotFoundException e) {
        	NotificationUtil.showInstant("error", "Server error");
        	//	Clients.showNotification("JDBC Driver not found", Clients.NOTIFICATION_TYPE_ERROR, null, 100, 100, 2000);
        	//	System.err.println("❌ JDBC Driver not found: " + e.getMessage());
        	logger.fatal("❌ JDBC Driver not found: {}", e);
        } catch (SQLException e) {
       // 	Clients.showNotification("❌ Database Connection Error:", Clients.NOTIFICATION_TYPE_ERROR, null, 100, 100, 2000);
       //     System.err.println("❌ Database Connection Error: " + e.getMessage());
            logger.fatal("❌ Database Connection Error: {}", e);
        }
        return connection;
    }
    public static void main(String args[]) {
    	getConnection();
    }

}


