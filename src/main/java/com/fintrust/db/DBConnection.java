package com.fintrust.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Single ton class Establishing DBConnection by hard-coded
 */
public class DBConnection {

	private static Connection connection = null;

	// Private constructor to prevent external instantiation
	private DBConnection() {
	}

	private static final Logger logger = LogManager.getLogger(DBConnection.class);

	/**
	 * Establishing connection and returning connection object
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Connection getConnection() {
		// Establish the connection
		try {
			if (connection == null || connection.isClosed()) {
				// Load the properties file
				Properties properties = new Properties();
				// Load from classpath
				try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {

					if (is == null) {
						logger.error("dbconfig.properties not found in classpath");

						return null;
					}

					properties.load(is);
				} catch (IOException e) {
					logger.error("Faild to load properties file");
					e.printStackTrace();
				}

				// Get the properties from the file
				String dbUrl = properties.getProperty("db.url");
				String dbUsername = properties.getProperty("db.username");
				String dbPassword = properties.getProperty("db.password");
				String dbDriverClassName = properties.getProperty("db.driverClassName");

				// Load the database driver
				Class.forName(dbDriverClassName);

				connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Database driver class not found.", e);
		} catch (SQLException e) {
			logger.error("SQL Exception.", e);
		}
		logger.info("Database connection established.");

		return connection;
	}

	// Close the connection
	public static void closeConnection() throws SQLException {
		if (connection != null && !connection.isClosed()) {
			connection.close();

			logger.info("Database connection closed.");
		}
	}

}
