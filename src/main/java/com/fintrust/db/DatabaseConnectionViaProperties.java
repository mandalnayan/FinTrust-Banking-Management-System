package com.fintrust.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionViaProperties {

    private static Connection connection = null;

    /**
     * Establishing connection and returning connection object
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null) {
            // Load the properties file
            Properties properties = new Properties();
            try (FileInputStream inputStream = new FileInputStream("src/main/resources/dbconfig.properties")) {
                properties.load(inputStream);
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
                throw new SQLException("Database driver class not found.", e);
            }

            // Establish the connection
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Database connection established.");
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
    	try {
			Connection  connection = getConnection();
			System.out.println(connection != null);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
    }
}

