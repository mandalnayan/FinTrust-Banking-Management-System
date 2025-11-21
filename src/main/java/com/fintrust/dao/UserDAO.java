package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fintrust.model.User;

/**
 * DAO interface for managing users in the banking system.
 * <p>
 * This interface defines standard CRUD operations following
 * banking-grade secure practices.
 */
public interface UserDAO {

    /**
     * Inserts a new user into the database.
     *
     * @param User contains all information about user
     * @throws SQLException if database operation fails
     */
    long create(User user);
    
    /**
     * Check either email already exist
     */
    
    boolean isEmailExists(String email);

    /**
     * Finds a user by ID.
     *
     * @param userId the user ID
     * @return a Map representing column names and values, or null if not found
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long userId) throws SQLException;

    /**
     * Retrieves all users.
     *
     * @return list of Maps representing user records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates user details (except password).
     *
     * @param userId user ID
     * @param email  new email
     * @param phone  new phone
     * @param role   new role
     * @param status new status
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long userId, String email, String phone, String role, String status) throws SQLException;

    /**
     * Updates only the password hash for a user.
     *
     * @param userId      user ID
     * @param passwordHash new hashed password
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updatePassword(long userId, String passwordHash) throws SQLException;

    /**
     * Deletes a user record. Banking systems recommend soft delete,
     * but this performs hard delete.
     *
     * @param userId user ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long userId) throws SQLException;
}
