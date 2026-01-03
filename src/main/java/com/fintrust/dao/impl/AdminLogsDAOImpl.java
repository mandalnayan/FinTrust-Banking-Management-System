package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.AdminLogsDAO;

/**
 * JDBC implementation of AdminLogsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class AdminLogsDAOImpl implements AdminLogsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public AdminLogsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long adminId, String action, String metadata) throws SQLException {
        String sql = """
            INSERT INTO admin_logs
            (admin_id, action, metadata)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, adminId);
            ps.setString(2, action);
            if (metadata != null) ps.setString(3, metadata); else ps.setNull(3, Types.VARCHAR);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long logId) throws SQLException {
        String sql = "SELECT * FROM admin_logs WHERE log_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, logId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByAdminId(long adminId) throws SQLException {
        String sql = "SELECT * FROM admin_logs WHERE admin_id = ? ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM admin_logs ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean delete(long logId) throws SQLException {
        String sql = "DELETE FROM admin_logs WHERE log_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, logId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the admin log record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("log_id", rs.getLong("log_id"));
        map.put("admin_id", rs.getLong("admin_id"));
        map.put("action", rs.getString("action"));
        map.put("metadata", rs.getString("metadata"));
        map.put("created_at", rs.getTimestamp("created_at"));
        return map;
    }
}
