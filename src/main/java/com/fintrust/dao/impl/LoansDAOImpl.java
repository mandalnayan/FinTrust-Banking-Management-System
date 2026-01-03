package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.LoansDAO;

/**
 * JDBC implementation of LoansDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class LoansDAOImpl implements LoansDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public LoansDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, String loanType, double principal,
                       double interestRate, int tenureMonths, String status) throws SQLException {

        String sql = """
            INSERT INTO loans
            (user_id, loan_type, principal_amount, interest_rate, tenure_months, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, loanType != null ? loanType.toLowerCase() : "personal");
            ps.setDouble(3, principal);
            ps.setDouble(4, interestRate);
            ps.setInt(5, tenureMonths);
            ps.setString(6, status != null ? status.toLowerCase() : "applied");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long loanId) throws SQLException {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM loans WHERE user_id = ? ORDER BY created_at DESC";
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
        String sql = "SELECT * FROM loans ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean updateStatus(long loanId, String status, Timestamp approvedAt) throws SQLException {
        String sql = "UPDATE loans SET status = ?, approved_at = ? WHERE loan_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toLowerCase());
            if (approvedAt != null) ps.setTimestamp(2, approvedAt); else ps.setNull(2, Types.TIMESTAMP);
            ps.setLong(3, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long loanId) throws SQLException {
        String sql = "DELETE FROM loans WHERE loan_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the loan record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("loan_id", rs.getLong("loan_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("loan_type", rs.getString("loan_type"));
        map.put("principal_amount", rs.getBigDecimal("principal_amount"));
        map.put("interest_rate", rs.getBigDecimal("interest_rate"));
        map.put("tenure_months", rs.getInt("tenure_months"));
        map.put("status", rs.getString("status"));
        map.put("created_at", rs.getTimestamp("created_at"));
        map.put("approved_at", rs.getTimestamp("approved_at"));
        return map;
    }
}

