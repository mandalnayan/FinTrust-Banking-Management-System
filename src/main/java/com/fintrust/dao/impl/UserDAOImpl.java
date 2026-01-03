package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import com.fintrust.dao.UserDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.User.Status;

/**
 * JDBC implementation of UserDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements and follows
 * banking standards.
 */
public class UserDAOImpl implements UserDAO {

	private Connection connection;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param connection JDBC connection managed externally
	 */
	public UserDAOImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Long create(User user) throws SQLException {
		if (connection == null || connection.isClosed()) connection = DBConnection.getConnection();
		String sql = """
				    INSERT INTO users (full_name, email, phone, password_hash, role)
				    VALUES (?, ?, ?, ?, ?)
				""";

		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, user.getFullName());
			ps.setString(2, user.getEmail());
			ps.setString(3, user.getPhone());
			ps.setString(4, user.getPassword());
			ps.setString(5, user.getRole().name().toLowerCase());

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next())
					return rs.getLong(1);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return -1l;
	}

	@Override
	public Boolean isEmailExists(String email) throws SQLException {

		if (connection == null || connection.isClosed()) connection = DBConnection.getConnection();
		String sql = "SELECT * FROM users WHERE email = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		}
	}

	@Override
	public User authenticate(String userName, String password) throws SQLException {
		String sql = "SELECT * FROM users WHERE email = ? and password_hash = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, userName);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = new User(rs.getLong("user_id"), rs.getString("full_name"), rs.getString("email"),
							rs.getString("phone"), rs.getString("role"), rs.getString("status"),
							rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"));
					return user;
				}
			}
			return null;
		}
	}

	@Override
	public User findById(long userId) throws SQLException {
		String sql = "SELECT * FROM users WHERE user_id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapRowToUser(rs);
			}
		}
		return null;
	}
	
	@Override
	public User findByEmail(String email) throws SQLException {
		String sql = "SELECT * FROM users WHERE email = ?";

		try (Connection connection = DBConnection.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, email);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return mapRowToUser(rs);
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> findAll() {
		String sql = "SELECT * FROM users ORDER BY email ASC";
		List<Map<String, Object>> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				list.add(mapRow(rs));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	@Override
	public boolean update(User user) throws SQLException {

    	String sql = "UPDATE users SET full_name = ?, phone = ?,kyc_status = ? WHERE user_id = ? ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
        	
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, "UPDATED");
            ps.setLong(4, user.getId());

            return ps.executeUpdate() > 0;
        }
	}
	
	@Override
	public boolean updateKycStatus(Long userId, String status) throws SQLException {

		String sql = "UPDATE users SET kyc_status = ? WHERE user_id = ? ";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, status);
			ps.setLong(2, userId);

			return ps.executeUpdate() > 0;
		}

	}

	@Override
	public boolean updatePassword(String email, String passwordHash) throws SQLException {
		String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
		
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, passwordHash);
			ps.setString(2, email);

			return ps.executeUpdate() > 0;
		}
	}

	@Override
	public boolean delete(long userId) throws SQLException {
		String sql = "DELETE FROM users WHERE user_id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, userId);
			return ps.executeUpdate() > 0;
		}
	}
	
	@Override
	public Long getTotalUsers() throws SQLException {
		
		String sql = "Select count(*) from users where role = ?";
		
		
		try(PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, User.Role.ROLE_USER.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) return rs.getLong(1);
		}
		return 0l;
	}

	/**
	 * Maps a ResultSet row into a Map representing the user record.
	 *
	 * @param rs ResultSet positioned at the row
	 * @return Map with column names as keys and values as map values
	 * @throws SQLException if column access fails
	 */
	private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
		Map<String, Object> userMap = new HashMap<>();
		userMap.put("user_id", rs.getLong("user_id"));
		userMap.put("email", rs.getString("email"));
		userMap.put("phone", rs.getString("phone"));
		userMap.put("password_hash", rs.getString("password_hash"));
		userMap.put("role", rs.getString("role"));
		userMap.put("status", rs.getString("status"));
		userMap.put("created_at", rs.getTimestamp("created_at"));
		userMap.put("updated_at", rs.getTimestamp("updated_at"));
		return userMap;
	}
	
	/**
	 * Converting Row to User
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private User mapRowToUser(ResultSet rs) throws SQLException {
	    User user = new User(
	        rs.getLong("user_id"),
	        rs.getString("full_name"),
	        rs.getString("email"),
	        rs.getString("phone"),
	        rs.getString("role"),
	        rs.getString("status"),
	        rs.getTimestamp("created_at"),	      
	        rs.getTimestamp("updated_at")
	    );
	    user.setKycStatus(User.KycStatus.valueOf(rs.getString("kyc_status").toUpperCase()));
	    user.setPassword(rs.getString("password_hash"));
	    return user;
	}

	@Override
	public boolean updateUserStatus(long userId , Status updatedStatus) throws SQLException {
		String sql = "UPDATE users SET status = ? WHERE user_id = ?";
		
		try (Connection con = DBConnection.getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, updatedStatus.toString().toLowerCase());
			ps.setLong(2, userId);
			if(ps.executeUpdate() > 0) {
				return true;
			}
			return false;
		}
	}

	
	@Override
	public List<User> findAllUsers() throws SQLException {
		List<User> allUser = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE role='ROLE_USER' ORDER BY user_id DESC";

		try (Connection con = DBConnection.getConnection(); 
			PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				User u = new User();
				u.setId(rs.getLong("user_id"));
				u.setFullName(rs.getString("full_name"));
				u.setEmail(rs.getString("email"));
				u.setPhone(rs.getString("phone"));
				u.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
				u.setKycStatus(User.KycStatus.valueOf(rs.getString("kyc_status").toUpperCase()));
				allUser.add(u);
			}
		}
		return allUser;
	}
}
