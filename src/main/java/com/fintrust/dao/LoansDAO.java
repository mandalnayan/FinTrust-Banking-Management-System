package com.fintrust.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing loans in the banking system.
 * <p>
 * All methods follow banking-grade secure JDBC standards.
 */
public interface LoansDAO {

    /**
     * Applies for a new loan.
     *
     * @param userId        ID of the user
     * @param loanType      type of loan ('home','personal','education','vehicle','business')
     * @param principal     principal amount
     * @param interestRate  interest rate (in percentage)
     * @param tenureMonths  loan tenure in months
     * @param status        loan status ('applied','approved','active','closed','defaulted')
     * @return generated loan_id
     * @throws SQLException if database operation fails
     */
    long create(long userId, String loanType, double principal,
                double interestRate, int tenureMonths, String status) throws SQLException;

    /**
     * Finds a loan by loan_id.
     *
     * @param loanId loan ID
     * @return Map representing the loan record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long loanId) throws SQLException;

    /**
     * Finds all loans of a user.
     *
     * @param userId user ID
     * @return list of loan records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByUserId(long userId) throws SQLException;

    /**
     * Retrieves all loans in the system.
     *
     * @return list of loan records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates loan status and approval date.
     *
     * @param loanId     loan ID
     * @param status     new status
     * @param approvedAt approval timestamp (nullable)
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateStatus(long loanId, String status, java.sql.Timestamp approvedAt) throws SQLException;

    /**
     * Deletes a loan record.
     *
     * @param loanId loan ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long loanId) throws SQLException;
}

