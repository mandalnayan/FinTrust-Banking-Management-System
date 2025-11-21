package com.fintrust.dao;



import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing admin activity logs in the banking system.
 * <p>
 * All operations follow banking-grade secure JDBC standards.
 */
public interface AdminLogsDAO {

    /**
     * Creates a new admin log entry.
     *
     * @param adminId ID of the admin performing the action
     * @param action  description of the action performed
     * @param metadata optional JSON metadata for the action
     * @return generated log_id
     * @throws SQLException if database operation fails
     */
    long create(long adminId, String action, String metadata) throws SQLException;

    /**
     * Finds a log entry by log_id.
     *
     * @param logId log ID
     * @return Map representing the log record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long logId) throws SQLException;

    /**
     * Retrieves all logs by a specific admin.
     *
     * @param adminId admin ID
     * @return list of log records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByAdminId(long adminId) throws SQLException;

    /**
     * Retrieves all admin logs in the system.
     *
     * @return list of log records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Deletes a log entry.
     *
     * @param logId log ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long logId) throws SQLException;
}
