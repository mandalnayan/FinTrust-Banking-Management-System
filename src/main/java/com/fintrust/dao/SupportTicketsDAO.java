package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing customer support tickets in the banking system.
 * <p>
 * All operations follow banking-grade secure JDBC standards.
 */
public interface SupportTicketsDAO {

    /**
     * Creates a new support ticket for a user.
     *
     * @param userId   ID of the user creating the ticket
     * @param subject  ticket subject/title
     * @param message  detailed message or issue description
     * @param priority ticket priority ('low','medium','high')
     * @param status   ticket status ('open','in_progress','resolved','closed')
     * @return generated ticket_id
     * @throws SQLException if database operation fails
     */
    long create(long userId, String subject, String message, String priority, String status) throws SQLException;

    /**
     * Finds a support ticket by its ID.
     *
     * @param ticketId ticket ID
     * @return Map representing the ticket record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long ticketId) throws SQLException;

    /**
     * Retrieves all tickets created by a specific user.
     *
     * @param userId user ID
     * @return list of ticket records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all support tickets in the system.
     *
     * @return list of ticket records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates the status and priority of a support ticket.
     *
     * @param ticketId ticket ID
     * @param status   new ticket status
     * @param priority new ticket priority
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long ticketId, String status, String priority) throws SQLException;

    /**
     * Deletes a support ticket.
     *
     * @param ticketId ticket ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long ticketId) throws SQLException;
}
