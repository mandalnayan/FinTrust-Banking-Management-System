package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import com.fintrust.dao.UserDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;

/**
 * JDBC implementation of UserDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements and follows
 * banking standards.
 */
public class UserDAOImpl implements UserDAO {

	private final Connection connection;

	/**
	 * Constructor for dependency injection.
	 *
	 * @param connection JDBC connection managed externally
	 */
	public UserDAOImpl() {
		this.connection = DBConnection.getConnection();
	}

	@Override
	public long create(User user) {

		String sql = """
				    INSERT INTO users (full_name, phone, password_hash, role)
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
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return -1;
	}

	@Override
	public boolean isEmailExists(String email) {
		String sql = "SELECT * FROM users WHERE email = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean authenticate(String userName, String password) throws SQLException {
		String sql = "SELECT * FROM users WHERE userName = ? and password = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, userName);
			ps.setString(2, password);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	public User findById(long userId) throws SQLException {
		String sql = "SELECT * FROM users WHERE user_id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setLong(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return (User) mapRow(rs).get("userId");
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

//    	will work  **********
	@Override
	public boolean update(User user) throws SQLException {

//    	String sql = """
//            UPDATE users SET
//        		full_name = ?,
//                phone = ?,
//                role = ?,
//                status = ?
//            WHERE user_id = ?
//        """;
//
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//        	
//            ps.setString(1, user.getPhone());
//            ps.setString(3, role.toLowerCase());
//            ps.setString(4, status.toLowerCase());
//            ps.setLong(5, userId);
//
//            return ps.executeUpdate() > 0;
//        }

		return false;
	}

	@Override
	public boolean updatePassword(long userId, String passwordHash) throws SQLException {
		String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setString(1, passwordHash);
			ps.setLong(2, userId);

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
}
