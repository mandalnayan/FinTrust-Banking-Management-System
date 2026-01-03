package com.fintrust.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing login history in the banking system.
 * <p>
 * All operations follow banking-grade secure JDBC standards.
 */
public interface LoginHistoryDAO {

    /**
     * Records a login attempt for a user.
     *
     * @param userId     ID of the user
     * @param ipAddress  IP address of login attempt
     * @param deviceInfo Device information (browser, OS, etc.)
     * @param status     login status ('success','failed')
     * @return generated login_id
     * @throws SQLException if database operation fails
     */
    long create(long userId, String ipAddress, String deviceInfo, String status) throws SQLException;

    /**
     * Finds a login history entry by login_id.
     *
     * @param loginId login ID
     * @return Map representing the login record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long loginId) throws SQLException;

    /**
     * Retrieves all login history for a specific user.
     *
     * @param userId user ID
     * @return list of login records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all login history entries in the system.
     *
     * @return list of login records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Deletes a login history entry.
     *
     * @param loginId login ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long loginId) throws SQLException;
}
