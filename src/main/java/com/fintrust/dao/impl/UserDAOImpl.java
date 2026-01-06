package com.fintrust.dao.impl;

import java.sql.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.dao.UserDAO;
import com.fintrust.db.DBConnection;
import com.fintrust.model.User;
import com.fintrust.model.User.Status;

/**
 * JDBC implementation of {@link UserDAO}.
 * <p>
 * Handles persistence, authentication, KYC status, and lifecycle operations
 * for users in a banking system. Uses PreparedStatements to prevent SQL injection
 * and follows audit-friendly logging practices.
 */
public class UserDAOImpl implements UserDAO {

    private static final Logger logger = LogManager.getLogger(UserDAOImpl.class);

    /** JDBC connection managed externally */
    private Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param connection JDBC connection managed externally
     */
    public UserDAOImpl(Connection connection) {
        this.connection = connection;
        logger.debug("UserDAOImpl initialized.");
    }

    @Override
    public Long create(User user) throws SQLException {

        if (connection == null || connection.isClosed()) {
            connection = DBConnection.getConnection();
        }

        String sql = """
                INSERT INTO users (full_name, email, phone, password_hash, role)
                VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name().toLowerCase());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long userId = rs.getLong(1);
                    logger.info("User created successfully. userId={}", userId);
                    return userId;
                }
            }
        }

        logger.warn("User creation failed for email={}", user.getEmail());
        return -1L;
    }

    @Override
    public Boolean isEmailExists(String email) throws SQLException {

        if (connection == null || connection.isClosed()) {
            connection = DBConnection.getConnection();
        }

        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            boolean exists = ps.executeQuery().next();
            logger.debug("Email existence check result={} for email={}", exists, email);
            return exists;
        }
    }

    @Override
    public User authenticate(String userName, String password) throws SQLException {

        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, userName);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.info("User authenticated successfully. email={}", userName);
                    return mapRowToUser(rs);
                }
            }
        }

        logger.warn("Authentication failed for email={}", userName);
        return null;
    }

    @Override
    public User findById(long userId) throws SQLException {

        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("User found for userId={}", userId);
                    return mapRowToUser(rs);
                }
            }
        }

        logger.warn("User not found for userId={}", userId);
        return null;
    }

    @Override
    public String findPasswordById(long userId) throws SQLException {

        String sql = "SELECT password_hash FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }

        logger.warn("Password not found for userId={}", userId);
        return null;
    }

    @Override
    public User findByEmail(String email) throws SQLException {

        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.debug("User found for email={}", email);
                    return mapRowToUser(rs);
                }
            }
        }

        logger.warn("User not found for email={}", email);
        return null;
    }

    @Override
    public List<Map<String, Object>> findAll() {

        String sql = "SELECT * FROM users ORDER BY email ASC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            logger.error("Error while fetching all users.", ex);
        }

        logger.info("Fetched {} users.", list.size());
        return list;
    }

    @Override
    public boolean update(User user) throws SQLException {

        String sql = "UPDATE users SET full_name = ?, phone = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setLong(3, user.getId());

            boolean updated = ps.executeUpdate() > 0;
            logger.info("User update result={} for userId={}", updated, user.getId());
            return updated;
        }
    }

    @Override
    public String getUserKycStatus(Long userId) throws SQLException {

        String sql = "SELECT kyc_status FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
        }

        logger.warn("KYC status not found for userId={}", userId);
        return null;
    }

    @Override
    public Long getNumberOfPendingKycRequest() throws SQLException {

        String sql = "SELECT COUNT(*) FROM users WHERE kyc_status = 'REQUESTED'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return null;
    }

    @Override
    public boolean updateKycStatus(Long userId, User.KycStatus status) throws SQLException {

        String sql = "UPDATE users SET kyc_status = ? WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setLong(2, userId);

            boolean updated = ps.executeUpdate() > 0;
            logger.info("KYC status updated to {} for userId={}", status, userId);
            return updated;
        }
    }

    @Override
    public boolean updatePassword(String email, String passwordHash) throws SQLException {

        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setString(2, email);

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Password updated for email={}", email);
            return updated;
        }
    }

    @Override
    public boolean delete(long userId) throws SQLException {

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            boolean deleted = ps.executeUpdate() > 0;
            logger.info("User delete result={} for userId={}", deleted, userId);
            return deleted;
        }
    }

    @Override
    public Long getTotalUsers() throws SQLException {

        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, User.Role.ROLE_USER.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0L;
    }

    /**
     * Maps a ResultSet row into a Map representation.
     */
    private Map<String, Object> mapRow(ResultSet rs) throws SQLException {

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user_id", rs.getLong("user_id"));
        userMap.put("email", rs.getString("email"));
        userMap.put("phone", rs.getString("phone"));
        userMap.put("password_hash", rs.getString("password_hash"));
        userMap.put("role", rs.getString("role"));
        userMap.put("status", rs.getString("status"));
        userMap.put("created_at", rs.getTimestamp("created_at"));
        userMap.put("updated_at", rs.getTimestamp("updated_at"));
        return userMap;
    }

    /**
     * Converts ResultSet row into {@link User} domain object.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {

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

        user.setKycStatus(User.KycStatus.valueOf(rs.getString("kyc_status").toUpperCase()));
        user.setPassword(rs.getString("password_hash"));
        return user;
    }

    @Override
    public boolean updateUserStatus(long userId, Status updatedStatus) throws SQLException {

        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, updatedStatus.name().toUpperCase());
            ps.setLong(2, userId);

            boolean updated = ps.executeUpdate() > 0;
            logger.info("User status updated to {} for userId={}", updatedStatus, userId);
            return updated;
        }
    }

    @Override
    public List<User> findAllUsers() throws SQLException {

        List<User> allUser = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role='ROLE_USER' ORDER BY user_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPhone(rs.getString("phone"));
                u.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
                u.setKycStatus(User.KycStatus.valueOf(rs.getString("kyc_status").toUpperCase()));
                allUser.add(u);
            }
        }

        logger.info("Fetched {} users with ROLE_USER.", allUser.size());
        return allUser;
    }
}
