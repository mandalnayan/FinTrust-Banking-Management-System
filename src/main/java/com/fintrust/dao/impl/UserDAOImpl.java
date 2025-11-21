package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.UserDAO;

/**
 * JDBC implementation of UserDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements
 * and follows banking standards.
 */
public class UserDAOImpl implements UserDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(String email, String phone, String passwordHash,
                       String role, String status) throws SQLException {

        String sql = """
            INSERT INTO users (email, phone, password_hash, role, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, passwordHash);
            ps.setString(4, role.toLowerCase());
            ps.setString(5, status.toLowerCase());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY email ASC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public boolean update(long userId, String email, String phone, String role, String status) throws SQLException {
        String sql = """
            UPDATE users SET
                email = ?,
                phone = ?,
                role = ?,
                status = ?
            WHERE user_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setString(3, role.toLowerCase());
            ps.setString(4, status.toLowerCase());
            ps.setLong(5, userId);

            return ps.executeUpdate() > 0;
        }
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
