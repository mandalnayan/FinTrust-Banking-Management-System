package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import com.fintrust.dao.BeneficiaryDAO;
import com.fintrust.model.Beneficiary;

/**
 * JDBC implementation of BeneficiariesDAO for banking systems.
 * <p>
 * All CRUD operations are implemented securely using PreparedStatements
 * and follow banking standards.
 */
public class BeneficiaryDAOImpl implements BeneficiaryDAO {

    private final Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public BeneficiaryDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(Beneficiary beneficiary) throws SQLException {

        String sql = """
            INSERT INTO beneficiaries (user_id, name, account_number, bank_name, ifsc_code)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, beneficiary.getUserId());
            ps.setString(2, beneficiary.getName());
            ps.setLong(3, beneficiary.getAccountNumber());
            ps.setString(4, beneficiary.getBankName());
            ps.setString(5, beneficiary.getIfscCode());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Beneficiary findById(long beneficiaryId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE beneficiary_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, beneficiaryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowtoModel(rs);
            }
        }
        return null;
    }

    @Override
    public List<Beneficiary> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM beneficiaries WHERE user_id = ?";
        List<Beneficiary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowtoModel(rs));
                }
            }
        }

        return list;
    }

    @Override
    public List<Beneficiary> findAll() throws SQLException {
        String sql = "SELECT * FROM beneficiaries ORDER BY beneficiary_id ASC";
        List<Beneficiary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowtoModel(rs));
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
    
    /**
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    private Beneficiary mapRowtoModel(ResultSet rs) throws SQLException {
        Beneficiary b = new Beneficiary();

        b.setBeneficiaryId(rs.getLong("beneficiary_id"));
        b.setUserId(rs.getLong("user_id"));
        b.setName(rs.getString("name"));

        // account_number is BIGINT UNSIGNED → use getLong()
        b.setAccountNumber(rs.getLong("account_number"));

        b.setBankName(rs.getString("bank_name"));
        b.setIfscCode(rs.getString("ifsc_code"));
        b.setAddedAt(rs.getTimestamp("added_at"));

        return b;
    }

}
