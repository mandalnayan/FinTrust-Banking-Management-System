package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    long create(long userId, String gender, java.sql.Date dob,
                String aadhaarMask, String panMask,
                String country, String state, String district,
                String city, String pincode) throws SQLException;

    /**
     * Finds a user details record by details_id.
     *
     * @param detailsId primary key
     * @return Map representing the record, or null if not found
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long detailsId) throws SQLException;

    /**
     * Finds details by user_id.
     *
     * @param userId user ID
     * @return Map representing the record, or null if not found
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all user details records.
     *
     * @return list of Maps representing user details
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates user details.
     *
     * @param detailsId    ID of the details record
     * @param gender       'male', 'female', 'other'
     * @param dob          date of birth
     * @param aadhaarMask  masked Aadhaar number
     * @param panMask      masked PAN number
     * @param country      country
     * @param state        state
     * @param district     district
     * @param city         city
     * @param pincode      postal code
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long detailsId, String gender, java.sql.Date dob,
                   String aadhaarMask, String panMask,
                   String country, String state, String district,
                   String city, String pincode) throws SQLException;

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

