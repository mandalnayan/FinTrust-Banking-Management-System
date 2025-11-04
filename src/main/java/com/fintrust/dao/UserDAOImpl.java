package com.fintrust.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.zkoss.zk.ui.util.Clients;

import com.fintrust.db.DBConnection;
import com.fintrust.model.User;

public class UserDAOImpl implements UserDAO {

	@Override
	public boolean saveUser(User user) {
		// Execute this command first time while table creation
	   //	createUserTable();
		
		String sql = "INSERT INTO users(name, email, phone, gender, country, state, dist, city, pincode, dob, password) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, user.getName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPhone());
			ps.setString(4, user.getGender());
			ps.setString(5, user.getCountry());
			ps.setString(6, user.getState());
			ps.setString(7, user.getDist());
			ps.setString(8, user.getCity());
			ps.setString(9, user.getPincode());
			ps.setDate(10, user.getDob());
			ps.setString(11, user.getPassword());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isEmailExists(String email) {
		String sql = "SELECT email FROM users WHERE email=?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean createUserTable() {
		String query = "CREATE TABLE users (" + "id INT AUTO_INCREMENT PRIMARY KEY," + "name VARCHAR(100),"
				+ "email VARCHAR(100) UNIQUE," + "phone VARCHAR(15)," + "gender VARCHAR(10)," + "country VARCHAR(50),"
				+ "state VARCHAR(50)," + "dist VARCHAR(50)," + "city VARCHAR(50)," + "pincode VARCHAR(10),"
				+ "dob VARCHAR(20)," + "password VARCHAR(255)," + "registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
				+ "status VARCHAR(20) DEFAULT 'Active'" + ");";

		try (Connection con = DBConnection.getConnection();) {
			Statement stmt = con.createStatement();
			int row = stmt.executeUpdate(query);
			Clients.showNotification("Table created: " + row, true);
			return true;
		} catch (Exception e) {
			Clients.showNotification(e.getMessage());
			return false;
		}

	}
}
