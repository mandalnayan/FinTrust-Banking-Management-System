package com.fintrust.dao.impl;



import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.fintrust.dao.UserDetailsDAO;

/**
 * JDBC implementation of UserDetailsDAO for banking systems.
 * <p>
 * Implements all CRUD operations securely using PreparedStatements
 * and follows banking-grade standards.
 */
public class UserDetailsDAOImpl implements UserDetailsDAO {

    private final Connection connection;

    /**
     * Constructor injection of JDBC connection.
     *
     * @param connection managed externally
     */
    public UserDetailsDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long create(long userId, String gender, Date dob,
                       String aadhaarMask, String panMask,
                       String country, String state, String district,
                       String city, String pincode) throws SQLException {

        String sql = """
            INSERT INTO user_details
            (user_id, gender, dob, aadhaar_masked, pan_masked, country, state, district, city, pincode)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.setString(2, gender != null ? gender.toLowerCase() : null);
            ps.setDate(3, dob);
            ps.setString(4, aadhaarMask);
            ps.setString(5, panMask);
            ps.setString(6, country);
            ps.setString(7, state);
            ps.setString(8, district);
            ps.setString(9, city);
            ps.setString(10, pincode);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        return -1;
    }

    @Override
    public Map<String, Object> findById(long detailsId) throws SQLException {
        String sql = "SELECT * FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM user_details WHERE user_id = ?";

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
        String sql = "SELECT * FROM user_details ORDER BY details_id ASC";
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
    public boolean update(long detailsId, String gender, Date dob,
                          String aadhaarMask, String panMask,
                          String country, String state, String district,
                          String city, String pincode) throws SQLException {

        String sql = """
            UPDATE user_details SET
                gender = ?, dob = ?, aadhaar_masked = ?, pan_masked = ?,
                country = ?, state = ?, district = ?, city = ?, pincode = ?
            WHERE details_id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gender != null ? gender.toLowerCase() : null);
            ps.setDate(2, dob);
            ps.setString(3, aadhaarMask);
            ps.setString(4, panMask);
            ps.setString(5, country);
            ps.setString(6, state);
            ps.setString(7, district);
            ps.setString(8, city);
            ps.setString(9, pincode);
            ps.setLong(10, detailsId);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(long detailsId) throws SQLException {
        String sql = "DELETE FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Maps a ResultSet row into a Map representing the user_details record.
     *
     * @param rs ResultSet positioned at the row
     * @return Map with column names as keys and values as map values
     * @throws SQLException if column access fails
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("details_id", rs.getLong("details_id"));
        map.put("user_id", rs.getLong("user_id"));
        map.put("gender", rs.getString("gender"));
        map.put("dob", rs.getDate("dob"));
        map.put("aadhaar_masked", rs.getString("aadhaar_masked"));
        map.put("pan_masked", rs.getString("pan_masked"));
        map.put("country", rs.getString("country"));
        map.put("state", rs.getString("state"));
        map.put("district", rs.getString("district"));
        map.put("city", rs.getString("city"));
        map.put("pincode", rs.getString("pincode"));
        map.put("created_at", rs.getTimestamp("created_at"));
        map.put("updated_at", rs.getTimestamp("updated_at"));
        return map;
    }
}
