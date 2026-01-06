package com.fintrust.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.util.NotificationUtil;

/**
 * 
 * Establishing DBConnection by properties file
 */
public class DBConnectionViaProperties {
	  private static Connection connection = null;

	  private static final Logger logger = LogManager.getLogger(DBConnectionViaProperties.class);
	  
	    public static Connection getConnection(){
	        if (connection == null) {
	            // Load the properties file
	            Properties properties = new Properties();
	            try (FileInputStream inputStream = new FileInputStream("src/main/resources/dbconfig.properties")) {
	                properties.load(inputStream);
	            } catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

	            // Get the properties from the file
	            String dbUrl = properties.getProperty("db.url");
	            String dbUsername = properties.getProperty("db.username");
	            String dbPassword = properties.getProperty("db.password");
	            String dbDriverClassName = properties.getProperty("db.driverClassName");

	            // Load the database driver
	            try {
	                Class.forName(dbDriverClassName);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            }

	            // Establish the connection
	            try {
					connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
				}catch (SQLException e) {
				      
		        	logger.fatal("Database Connection Error: {}", e);
		        }
	            System.out.println(" Database Connected Successfully");     
                logger.info(" Database Connected Successfully. log");
	            
	        }
	        return connection;
	    }

	    // Close the connection
	    public static void closeConnection() throws SQLException {
	        if (connection != null && !connection.isClosed()) {
	            connection.close();
	            System.out.println("Database connection closed.");
	        }
	    }
	    
	    public static void main(String args[]) {
	    	
				Connection  connection = getConnection();
				System.out.println(connection != null);
			
	    }
	}


