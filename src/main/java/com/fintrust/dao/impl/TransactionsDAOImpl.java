package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.TransactionsDAO;

/**
 * JDBC implementation of TransactionsDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements
 * and follows banking-grade standards.
 */
public class TransactionsDAOImpl implements TransactionsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public TransactionsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long accountId, Long relatedAccountId, Long beneficiaryId,
                       String txnReference, String txnType, String mode,
                       double amount, double balanceAfter, String description,
                       String status) throws SQLException {

        String sql = """
            INSERT INTO transactions
            (account_id, related_account_id, beneficiary_id, txn_reference,
             txn_type, mode, amount, balance_after, description, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, accountId);
            if (relatedAccountId != null) ps.setLong(2, relatedAccountId); else ps.setNull(2, Types.BIGINT);
            if (beneficiaryId != null) ps.setLong(3, beneficiaryId); else ps.setNull(3, Types.BIGINT);
            ps.setString(4, txnReference);
            ps.setString(5, txnType.toLowerCase());
            ps.setString(6, mode.toLowerCase());
            ps.setDouble(7, amount);
            ps.setDouble(8, balanceAfter);
            ps.setString(9, description);
            ps.setString(10, status.toLowerCase());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long transactionId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }

        return null;
    }

    @Override
    public List<Map<String, Object>> findByAccountId(long accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions ORDER BY created_at DESC";
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
    public boolean updateStatus(long transactionId, String status) throws SQLException {
        String sql = "UPDATE transactions SET status = ? WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status.toLowerCase());
            ps.setLong(2, transactionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, transactionId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the transaction record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("transaction_id", rs.getLong("transaction_id"));
        map.put("account_id", rs.getLong("account_id"));
        map.put("related_account_id", rs.getObject("related_account_id"));
        map.put("beneficiary_id", rs.getObject("beneficiary_id"));
        map.put("txn_reference", rs.getString("txn_reference"));
        map.put("txn_type", rs.getString("txn_type"));
        map.put("mode", rs.getString("mode"));
        map.put("amount", rs.getBigDecimal("amount"));
        map.put("balance_after", rs.getBigDecimal("balance_after"));
        map.put("description", rs.getString("description"));
        map.put("status", rs.getString("status"));
        map.put("created_at", rs.getTimestamp("created_at"));
        return map;
    }
}
