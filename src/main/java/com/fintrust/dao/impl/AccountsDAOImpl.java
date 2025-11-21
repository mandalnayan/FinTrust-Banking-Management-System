package com.fintrust.dao.impl;


import java.sql.*;
import java.util.*;

import com.fintrust.dao.AccountsDAO;

/**
 * JDBC implementation of AccountsDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking standards.
 */
public class AccountsDAOImpl implements AccountsDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public AccountsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, long bankId, String accountNumber,
                       String accountType, double balance,
                       String currency, String status) throws SQLException {

        String sql = """
            INSERT INTO accounts
            (user_id, bank_id, account_number, account_type, balance, currency, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setLong(2, bankId);
            ps.setString(3, accountNumber);
            ps.setString(4, accountType.toLowerCase());
            ps.setDouble(5, balance);
            ps.setString(6, currency);
            ps.setString(7, status != null ? status.toLowerCase() : "active");

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long accountId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
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
        String sql = "SELECT * FROM accounts ORDER BY account_id ASC";
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
    public boolean update(long accountId, String accountType, String status, String currency) throws SQLException {
        String sql = """
            UPDATE accounts SET
                account_type = ?, status = ?, currency = ?
            WHERE account_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accountType.toLowerCase());
            ps.setString(2, status.toLowerCase());
            ps.setString(3, currency);
            ps.setLong(4, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateBalance(long accountId, double balance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setLong(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE account_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the account record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("account_id", rs.getLong("account_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("bank_id", rs.getLong("bank_id"));
        map.put("account_number", rs.getString("account_number"));
        map.put("account_type", rs.getString("account_type"));
        map.put("balance", rs.getBigDecimal("balance"));
        map.put("currency", rs.getString("currency"));
        map.put("status", rs.getString("status"));
        map.put("opened_at", rs.getTimestamp("opened_at"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        return map;
    }
}
