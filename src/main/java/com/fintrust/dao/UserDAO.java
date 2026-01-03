package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fintrust.model.User;
import com.fintrust.model.UserDetails;

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
    Long create(User user) throws SQLException; 
    
    /**
     * Check either email already exist
     */    
    Boolean isEmailExists(String email) throws SQLException; 
    
    /**
     * Authenticating user
     * @param userName
     * @param password
     * @return
     * @throws SQLException
     */
    User authenticate(String userName, String password) throws SQLException; 

    /**
     * Finds a user by ID.
     *
     * @param userId the user ID
     * @return a Map representing column names and values, or null if not found
     * @throws SQLException if database operation fails
     */
    User findById(long Id) throws SQLException;
    
    /**
     * Find user by email
     * @param eamil
     * @return
     * @throws SQLException
     */
    User findByEmail(String eamil) throws SQLException;

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
    boolean update(User user) throws SQLException;

    /**
     * Updates only the password hash for a user.
     *
     * @param userId      user ID
     * @param passwordHash new hashed password
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updatePassword(String email, String password) throws SQLException;

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
