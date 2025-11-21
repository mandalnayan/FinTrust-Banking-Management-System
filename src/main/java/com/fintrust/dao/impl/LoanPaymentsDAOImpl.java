package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.LoanPaymentsDAO;

/**
 * JDBC implementation of LoanPaymentsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking-grade standards.
 */
public class LoanPaymentsDAOImpl implements LoanPaymentsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public LoanPaymentsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long loanId, double amount, String method, String status) throws SQLException {
        String sql = """
            INSERT INTO loan_payments
            (loan_id, amount, method, status)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, loanId);
            ps.setDouble(2, amount);
            ps.setString(3, method != null ? method.toLowerCase() : "neft");
            ps.setString(4, status != null ? status.toLowerCase() : "success");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long paymentId) throws SQLException {
        String sql = "SELECT * FROM loan_payments WHERE payment_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByLoanId(long loanId) throws SQLException {
        String sql = "SELECT * FROM loan_payments WHERE loan_id = ? ORDER BY payment_date DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, loanId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM loan_payments ORDER BY payment_date DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    @Override
    public boolean updateStatus(long paymentId, String status) throws SQLException {
        String sql = "UPDATE loan_payments SET status = ? WHERE payment_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toLowerCase());
            ps.setLong(2, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long paymentId) throws SQLException {
        String sql = "DELETE FROM loan_payments WHERE payment_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the loan payment record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("payment_id", rs.getLong("payment_id"));
        map.put("loan_id", rs.getLong("loan_id"));
        map.put("amount", rs.getBigDecimal("amount"));
        map.put("payment_date", rs.getTimestamp("payment_date"));
        map.put("method", rs.getString("method"));
        map.put("status", rs.getString("status"));
        return map;
    }
}
