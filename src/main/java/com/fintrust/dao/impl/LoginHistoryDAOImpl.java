package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.LoginHistoryDAO;

/**
 * JDBC implementation of LoginHistoryDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class LoginHistoryDAOImpl implements LoginHistoryDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public LoginHistoryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, String ipAddress, String deviceInfo, String status) throws SQLException {
        String sql = """
            INSERT INTO login_history
            (user_id, ip_address, device_info, status)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, ipAddress);
            ps.setString(3, deviceInfo);
            ps.setString(4, status != null ? status.toLowerCase() : "success");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long loginId) throws SQLException {
        String sql = "SELECT * FROM login_history WHERE login_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM login_history WHERE user_id = ? ORDER BY occurred_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM login_history ORDER BY occurred_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean delete(long loginId) throws SQLException {
        String sql = "DELETE FROM login_history WHERE login_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loginId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the login history record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("login_id", rs.getLong("login_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("ip_address", rs.getString("ip_address"));
        map.put("device_info", rs.getString("device_info"));
        map.put("status", rs.getString("status"));
        map.put("occurred_at", rs.getTimestamp("occurred_at"));
        return map;
    }
}

