package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fintrust.model.UserDetails;

/**
 * DAO interface for managing user details in the banking system.
 * <p>
 * All methods follow banking-grade secure JDBC standards.
 */
public interface UserDetailsDAO {

    /**
     * Inserts a new user details record.
     *
     * @param userId       ID of the user (foreign key)
     * @param gender       'male', 'female', 'other'
     * @param dob          date of birth
     * @param aadhaarMask  masked Aadhaar number
     * @param panMask      masked PAN number
     * @param country      country
     * @param state        state
     * @param district     district
     * @param city         city
     * @param pincode      postal code
     * @return generated details_id
     * @throws SQLException if database operation fails
     */
    long create(UserDetails userDetails) throws SQLException;

    /**
     * Creating empty user details
     * @param userId
     * @return
     * @throws SQLException
     */
    Long createEmptyUserDetails(Long userId) throws SQLException;
    /**
     * Finds a user details record by details_id.
     *
     * @param detailsId primary key
     * @return Map representing the record, or null if not found
     * @throws SQLException if database operation fails
     */
    UserDetails findById(long detailsId) throws SQLException;

    /**
     * Finds details by user_id.
     *
     * @param userId user ID
     * @return Map representing the record, or null if not found
     * @throws SQLException if database operation fails
     */
    UserDetails findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all user details records.
     *
     * @return list of Maps representing user details
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

   
     /**
      * Update user details
      * @param userDetails
      * @return
      * @throws SQLException
      */
    boolean updateProfile(UserDetails userDetails) throws SQLException;
    
    /**
     * Saving or updating kyc form
     * @param userDetails
     * @return
     * @throws SQLException
     */
   boolean updateKyc(UserDetails userDetails) throws SQLException;
    
    /**
     * Update primary account of users
     * @param userId
     * @return
     * @throws SQLException
     */
    Long findPrimaryAccount(Long userId) throws SQLException;
    
    /**
     * Update primary account of users
     * @param userId
     * @param accountId
     * @return
     * @throws SQLException
     */
    boolean updatePrimaryAccount(Long userId, Long accountId) throws SQLException;

    /**
     * Deletes a user details record by details_id.
     * Note: ON DELETE RESTRICT at DB level.
     *
     * @param detailsId details ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long detailsId) throws SQLException;
}

