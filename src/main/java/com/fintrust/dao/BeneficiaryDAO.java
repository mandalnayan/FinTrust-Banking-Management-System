package com.fintrust.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fintrust.model.Beneficiary;

/**
 * DAO interface for managing user beneficiaries in the banking system.
 * <p>
 * All methods follow banking-grade secure JDBC standards.
 */
public interface BeneficiaryDAO {

    /**
     * Adds a new beneficiary for a user.
     *
     * @param userId        ID of the user
     * @param name          beneficiary name
     * @param accountNumber beneficiary account number
     * @param bankName      bank name (optional)
     * @param ifscCode      IFSC code (optional)
     * @return generated beneficiary_id
     * @throws SQLException if database operation fails
     */
    long create(Beneficiary beneficiaries) throws SQLException;

    /**
     * Finds a beneficiary by beneficiary_id.
     *
     * @param beneficiaryId primary key
     * @return Map representing the beneficiary record or null
     * @throws SQLException if database operation fails
     */
    Beneficiary findById(long beneficiaryId) throws SQLException;

    /**
     * Finds all beneficiaries of a user.
     *
     * @param userId user ID
     * @return list of beneficiary records
     * @throws SQLException if database operation fails
     */
    List<Beneficiary> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all beneficiaries.
     *
     * @return list of all beneficiary records
     * @throws SQLException if database operation fails
     */
    List<Beneficiary> findAll() throws SQLException;

    /**
     * Updates beneficiary information.
     *
     * @param beneficiaryId beneficiary ID
     * @param name          beneficiary name
     * @param accountNumber account number
     * @param bankName      bank name
     * @param ifscCode      IFSC code
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long beneficiaryId, String name, String accountNumber,
                   String bankName, String ifscCode) throws SQLException;

    /**
     * Deletes a beneficiary.
     *
     * @param beneficiaryId beneficiary ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long beneficiaryId) throws SQLException;
}

