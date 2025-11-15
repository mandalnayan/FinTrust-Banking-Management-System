package com.fintrust.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

import com.fintrust.db.DBConnection;
import com.fintrust.model.Customer;
import com.fintrust.model.User;

public class UserDAOImpl implements UserDAO {

	/**
	 * User: Take user data from signup form Save user data in db
	 */
	@Override
	public boolean saveUser(Customer user) {
		// Execute this command first time while table creation
		// createUserTable();
		long randomNo = (long)(Math.random()*10000);

		String sql = "INSERT INTO customer(customer_id, name, email, phone, gender, country, state, dist, city, pincode, dob, password) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, randomNo);
			ps.setString(2, user.getName());
			ps.setString(3, user.getEmail());
			ps.setString(4, user.getPhone());
			ps.setString(5, user.getGender());
			ps.setString(6, user.getCountry());
			ps.setString(7, user.getState());
			ps.setString(8, user.getDist());
			ps.setString(9, user.getCity());
			ps.setString(10, user.getPincode());
			ps.setDate(11, user.getDob());
			ps.setString(12, user.getPassword());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checking email is exit or not Use: While signup we make sure same email can't
	 * registered twich
	 */
	@Override
	public boolean isEmailExists(String email) {
		String sql = "SELECT email FROM customer WHERE email=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isAuthorize(String userName, String password) {
		try (Connection con = DBConnection.getConnection()) {
			PreparedStatement pstmt = con.prepareStatement("Select * from customer where email = ? and password = ?");
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Sessions.getCurrent().setAttribute("customer_id", rs.getLong(1));
				Sessions.getCurrent().setAttribute("userName", rs.getString(2));
				Clients.showNotification("Login Successfull! \n Welcome " + rs.getString(2), "info", null, "top_center",
						3000);

				return true;
			}
		} catch (SQLException e) {
			Clients.showNotification(e.getMessage());
		}
		return false;
	}

	public Customer getUserByEmail(String email) {
		String sql = "SELECT * FROM customer WHERE email=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			Customer customer = null;
			if (rs.next()) {
				customer = new Customer(rs.getLong("customer_id"), rs.getString("name"), rs.getString("email"),
						rs.getString("phone"), rs.getString("gender"), rs.getString("country"), rs.getString("state"),
						rs.getString("dist"), rs.getString("city"), rs.getString("pincode"), rs.getString("image"),
						rs.getDate("registered_date"), rs.getDate("dob"));
			}

			return customer;
		} catch (Exception e) {
			System.out.println("\nError: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean updateUser(Customer user) {
		String sql = "UPDATE customer SET name=?, phone=?, gender=?, country=?, state=?, city=?, pincode=?, dob=? WHERE email=?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, user.getName());
			ps.setString(2, user.getPhone());
			ps.setString(3, user.getGender());
			ps.setString(4, user.getCountry());
			ps.setString(5, user.getState());
			ps.setString(6, user.getCity());
			ps.setString(7, user.getPincode());
			ps.setDate(8, user.getDob());
			ps.setString(9, user.getEmail());

			int rows = ps.executeUpdate();
			return rows > 0;

		} catch (Exception e) {
			System.out.println("\nError: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean updatePassword(String password) {
		String sql = "SELECT password FROM customer WHERE email=?";
		String updateQ = "Update customer set password = ? where email = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			String email = (String) Sessions.getCurrent().getAttribute("currentUser");
			
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			Customer customer = null;
			if (rs.next()) {
				if (rs.getString(1).equals(password)) {
					Clients.showNotification("New password can't be same as old password..!", "info", null,
							"top_center", 3000);

				} else {
					PreparedStatement updatePS = conn.prepareStatement(updateQ);
					updatePS.setString(1, password);
					updatePS.setString(2, email);
					int updated = updatePS.executeUpdate();
					return updated > 0;
				}
			}
		} catch (Exception e) {
			System.out.println("\nError: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Creating table
	 * 
	 * @return
	 */
	private boolean createUserTable() {
		String query = "CREATE TABLE users (" + "id INT AUTO_INCREMENT PRIMARY KEY," + "name VARCHAR(100),"
				+ "email VARCHAR(100) UNIQUE," + "phone VARCHAR(15)," + "gender VARCHAR(10)," + "country VARCHAR(50),"
				+ "state VARCHAR(50)," + "dist VARCHAR(50)," + "city VARCHAR(50)," + "pincode VARCHAR(10),"
				+ "dob VARCHAR(20)," + "password VARCHAR(255)," + "image varchar(50)" + "twoFactor boolean"
				+ "registered_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," + "status VARCHAR(20) DEFAULT 'Active'" + ");";

		// Latest table code

		String newQuery = """
												CREATE TABLE `customer` (
				  `customer_id` bigint unsigned NOT NULL auto_increment,
				  `name` varchar(100) DEFAULT NULL,
				  `email` varchar(100) DEFAULT NULL,
				  `phone` varchar(15) DEFAULT NULL,
				  `gender` varchar(10) DEFAULT NULL,
				  `country` varchar(50) DEFAULT NULL,
				  `state` varchar(50) DEFAULT NULL,
				  `dist` varchar(50) DEFAULT NULL,
				  `city` varchar(50) DEFAULT NULL,
				  `pincode` varchar(10) DEFAULT NULL,
				  `dob` varchar(20) DEFAULT NULL,
				  `password` varchar(255) DEFAULT NULL,
				  `registered_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
				  `status` varchar(20) DEFAULT 'Active',
				  `image` varchar(50) DEFAULT NULL,
				  `twoFactor` tinyint(1) DEFAULT NULL,
				  PRIMARY KEY (`customer_id`),
				  UNIQUE KEY `email` (`email`)
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
												""";

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
