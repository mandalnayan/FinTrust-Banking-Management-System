package com.fintrust.dao;

	import com.fintrust.model.User;
	import java.sql.*;
	import java.util.*;

	public class UsersDAO_Copy {

	    private final Connection conn;

	    public UsersDAO(Connection conn) {
	        this.conn = conn;
	    }

	    public long create(User user) throws SQLException {
	        String sql = """
	            INSERT INTO users(full_name, email, phone, password_hash, role, status)
	            VALUES (?, ?, ?, ?, ?, ?)
	        """;

	        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	            ps.setString(1, user.getFullName());
	            ps.setString(2, user.getEmail());
	            ps.setString(3, user.getPhone());
	            ps.setString(4, user.getPasswordHash());
	            ps.setString(5, user.getRole());
	            ps.setString(6, user.getStatus());
	            ps.executeUpdate();

	            ResultSet rs = ps.getGeneratedKeys();
	            if (rs.next()) return rs.getLong(1);
	        }
	        return 0;
	    }

	    public User findById(long id) throws SQLException {
	        String sql = "SELECT * FROM users WHERE user_id = ?";
	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setLong(1, id);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                return map(rs);
	            }
	        }
	        return null;
	    }

	    public List<User> findAll() throws SQLException {
	        List<User> list = new ArrayList<>();
	        String sql = "SELECT * FROM users ORDER BY created_at DESC";

	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) list.add(map(rs));
	        }
	        return list;
	    }

	    private User map(ResultSet rs) throws SQLException {
	        User u = new User();
	        u.setUserId(rs.getLong("user_id"));
	        u.setFullName(rs.getString("full_name"));
	        u.setEmail(rs.getString("email"));
	        u.setPhone(rs.getString("phone"));
	        u.setPasswordHash(rs.getString("password_hash"));
	        u.setRole(rs.getString("role"));
	        u.setStatus(rs.getString("status"));
	        u.setCreatedAt(rs.getTimestamp("created_at").toInstant());
	        u.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
	        return u;
	    }
	}

