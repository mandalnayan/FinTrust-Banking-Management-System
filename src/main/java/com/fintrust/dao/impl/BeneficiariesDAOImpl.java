package com.fintrust.dao.impl;



import java.sql.*;
import java.util.*;

import com.fintrust.dao.BeneficiariesDAO;

/**
 * JDBC implementation of BeneficiariesDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking standards.
 */
public class BeneficiariesDAOImpl implements BeneficiariesDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public BeneficiariesDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, String name, String accountNumber,
                       String bankName, String ifscCode) throws SQLException {

        String sql = """
            INSERT INTO beneficiaries (user_id, name, account_number, bank_name, ifsc_code)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, name);
            ps.setString(3, accountNumber);
            ps.setString(4, bankName);
            ps.setString(5, ifscCode);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long beneficiaryId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE user_id = ?";
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
        String sql = "SELECT * FROM beneficiaries ORDER BY beneficiary_id ASC";
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
    public boolean update(long beneficiaryId, String name, String accountNumber,
                          String bankName, String ifscCode) throws SQLException {

        String sql = """
            UPDATE beneficiaries SET
                name = ?, account_number = ?, bank_name = ?, ifsc_code = ?
            WHERE beneficiary_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, accountNumber);
            ps.setString(3, bankName);
            ps.setString(4, ifscCode);
            ps.setLong(5, beneficiaryId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long beneficiaryId) throws SQLException {
        String sql = "DELETE FROM beneficiaries WHERE beneficiary_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the beneficiary record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("beneficiary_id", rs.getLong("beneficiary_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("name", rs.getString("name"));
        map.put("account_number", rs.getString("account_number"));
        map.put("bank_name", rs.getString("bank_name"));
        map.put("ifsc_code", rs.getString("ifsc_code"));
        map.put("added_at", rs.getTimestamp("added_at"));
        return map;
    }
}
