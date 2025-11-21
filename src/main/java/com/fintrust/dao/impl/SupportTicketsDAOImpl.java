package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.SupportTicketsDAO;

/**
 * JDBC implementation of SupportTicketsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class SupportTicketsDAOImpl implements SupportTicketsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public SupportTicketsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, String subject, String message, String priority, String status) throws SQLException {
        String sql = """
            INSERT INTO support_tickets
            (user_id, subject, message, priority, status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, subject);
            ps.setString(3, message);
            ps.setString(4, priority != null ? priority.toLowerCase() : "medium");
            ps.setString(5, status != null ? status.toLowerCase() : "open");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long ticketId) throws SQLException {
        String sql = "SELECT * FROM support_tickets WHERE ticket_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM support_tickets WHERE user_id = ? ORDER BY created_at DESC";
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
        String sql = "SELECT * FROM support_tickets ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean update(long ticketId, String status, String priority) throws SQLException {
        String sql = "UPDATE support_tickets SET status = ?, priority = ? WHERE ticket_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status != null ? status.toLowerCase() : "open");
            ps.setString(2, priority != null ? priority.toLowerCase() : "medium");
            ps.setLong(3, ticketId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long ticketId) throws SQLException {
        String sql = "DELETE FROM support_tickets WHERE ticket_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, ticketId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the support ticket record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("ticket_id", rs.getLong("ticket_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("subject", rs.getString("subject"));
        map.put("message", rs.getString("message"));
        map.put("priority", rs.getString("priority"));
        map.put("status", rs.getString("status"));
        map.put("created_at", rs.getTimestamp("created_at"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        return map;
    }
}
