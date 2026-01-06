package com.fintrust.dao.impl;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;

/**
 * JDBC implementation of {@link UserDetailsDAO}.
 * <p>
 * Handles persistence and retrieval of user personal and KYC-related details.
 * All database interactions use PreparedStatements and comply with
 * banking-grade security and audit standards.
 */
public class UserDetailsDAOImpl implements UserDetailsDAO {

    private static final Logger logger = LogManager.getLogger(UserDetailsDAOImpl.class);

    /** JDBC connection managed externally */
    private final Connection connection;

    /**
     * Constructor injection of JDBC connection.
     *
     * @param connection active JDBC connection
     */
    public UserDetailsDAOImpl(Connection connection) {
        this.connection = connection;
        logger.debug("UserDetailsDAOImpl initialized with JDBC connection.");
    }

    @Override
    public long create(UserDetails ud) throws SQLException {

        String sql = """
                INSERT INTO user_details
                (user_id, gender, dob, aadhaar_masked, pan_masked, country, state, city, pincode)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ud.getUserId());
            ps.setString(2, ud.getGender() != null ? ud.getGender().toLowerCase() : null);
            ps.setDate(3, ud.getDob() != null ? Date.valueOf(ud.getDob()) : null);
            ps.setString(4, ud.getAadhaarMasked());
            ps.setString(5, ud.getPanMasked());
            ps.setString(6, ud.getCountry());
            ps.setString(7, ud.getState());
            ps.setString(8, ud.getCity());
            ps.setString(10, ud.getPincode());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    logger.info("UserDetails created successfully. detailsId={}", id);
                    return id;
                }
            }
        }

        logger.warn("Failed to create UserDetails record.");
        return -1;
    }

    /**
     * Creates an empty user_details record during user onboarding.
     *
     * @param userId user ID
     * @return generated details ID
     * @throws SQLException if insert fails
     */
    public Long createEmptyUserDetails(Long userId) throws SQLException {

        String sql = """
                INSERT INTO user_details (user_id, created_at)
                VALUES (?, NOW())
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, userId);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);
                logger.info("Empty UserDetails created for userId={}", userId);
                return id;
            }
        }

        logger.warn("Failed to create empty UserDetails for userId={}", userId);
        return -1L;
    }

    @Override
    public UserDetails findById(long detailsId) throws SQLException {

        String sql = "SELECT * FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("UserDetails found for detailsId={}", detailsId);
                    return mapRowWithUserDetails(rs);
                }
            }
        }

        logger.warn("UserDetails not found for detailsId={}", detailsId);
        return null;
    }

    /**
     * Fetches complete user details by joining users and user_details tables.
     */
    @Override
    public UserDetails findByUserId(long userId) throws SQLException {

        String sql = """
                SELECT ud.*, us.*
                FROM user_details ud
                INNER JOIN users us ON ud.user_id = us.user_id
                WHERE ud.user_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("UserDetails fetched for userId={}", userId);
                    return mapRowWithUserDetails(rs);
                }
            }
        }

        logger.warn("UserDetails not found for userId={}", userId);
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

        logger.info("Fetched {} user_details records.", list.size());
        return list;
    }

    @Override
    public boolean updateProfile(UserDetails ud) throws SQLException {

        String sql = """
                UPDATE user_details SET
                    country = ?, state = ?, city = ?, pincode = ?
                WHERE details_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ud.getCountry());
            ps.setString(2, ud.getState());
            ps.setString(3, ud.getCity());
            ps.setString(4, ud.getPincode());
            ps.setLong(5, ud.getDetailsId());

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Profile update result={} for detailsId={}", updated, ud.getDetailsId());
            return updated;
        }
    }

    @Override
    public boolean updateKyc(UserDetails ud) throws SQLException {

        String sql = """
                UPDATE user_details SET
                    gender = ?,
                    dob = ?,
                    aadhaar_masked = ?,
                    pan_masked = ?,
                    country = ?,
                    state = ?,
                    district = ?,
                    city = ?,
                    pincode = ?,
                    address_proof_name = ?,
                    photo_name = ?,
                    updated_at = NOW()
                WHERE details_id = ?
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, ud.getGender().trim());
            if (ud.getDob() == null)
                return false;

            ps.setDate(2, Date.valueOf(ud.getDob()));
            ps.setString(3, ud.getAadhaarMasked().trim());
            ps.setString(4, ud.getPanMasked().trim());
            ps.setString(5, ud.getCountry().trim());
            ps.setString(6, ud.getState().trim());
            ps.setString(7, ud.getDistrict().trim());
            ps.setString(8, ud.getCity().trim());
            ps.setString(9, ud.getPincode().trim());
            ps.setString(10, ud.getAddressProofFileName());
            ps.setString(11, ud.getPhotoFileName());
            ps.setLong(12, ud.getDetailsId());

            boolean updated = ps.executeUpdate() > 0;
            logger.info("KYC update result={} for detailsId={}", updated, ud.getDetailsId());
            return updated;
        }
    }

    /**
     * Retrieves the primary account linked to a user.
     */
    public Long findPrimaryAccount(Long userId) throws SQLException {

        String sql = "SELECT * FROM user_details WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("primary_account_id");
            }
        }

        logger.warn("Primary account not found for userId={}", userId);
        return -1L;
    }

    /**
     * Updates primary account for a user.
     */
    public boolean updatePrimaryAccount(Long userId, Long accountId) throws SQLException {

        String sql = "UPDATE user_details SET primary_account_id = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            ps.setLong(2, userId);
            int rows = ps.executeUpdate();
            logger.info("Primary account update rowsAffected={} for userId={}", rows, userId);
            return rows > 0;
        }
    }

    @Override
    public boolean delete(long detailsId) throws SQLException {

        String sql = "DELETE FROM user_details WHERE details_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, detailsId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("UserDetails delete result={} for detailsId={}", deleted, detailsId);
            return deleted;
        }
    }

    /**
     * Maps ResultSet row to UserDetails domain model with User object.
     */
    private UserDetails mapRowWithUserDetails(ResultSet rs) throws SQLException {

        UserDetails ud = new UserDetails();
        ud.setDetailsId(rs.getLong("details_id"));
        ud.setGender(rs.getString("gender"));

        Date dob = rs.getDate("dob");
        if (dob != null)
            ud.setDob(dob.toLocalDate());

        ud.setAadhaarMasked(rs.getString("aadhaar_masked"));
        ud.setPanMasked(rs.getString("pan_masked"));
        ud.setCountry(rs.getString("country"));
        ud.setState(rs.getString("state"));
        ud.setDistrict(rs.getString("district"));
        ud.setCity(rs.getString("city"));
        ud.setPincode(rs.getString("pincode"));
        ud.setAddressProofFileName(rs.getString("address_proof_name"));
        ud.setPhotoFileName(rs.getString("photo_name"));
        ud.setPrimaryAccountId(rs.getLong("primary_account_id"));

        User user = new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role"),
                rs.getString("status"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );

        ud.setUser(user);
        return ud;
    }

    /**
     * Maps ResultSet row into key-value representation.
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
