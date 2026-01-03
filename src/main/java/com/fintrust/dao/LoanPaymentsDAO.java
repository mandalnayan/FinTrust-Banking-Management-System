package com.fintrust.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing loan payments in the banking system.
 * <p>
 * All operations follow banking-grade secure JDBC standards.
 */
public interface LoanPaymentsDAO {

    /**
     * Records a loan payment.
     *
     * @param loanId       ID of the loan
     * @param amount       payment amount
     * @param method       payment method ('neft','rtgs','imps','upi','card','cash')
     * @param status       payment status ('success','failed','pending')
     * @return generated payment_id
     * @throws SQLException if database operation fails
     */
    long create(long loanId, double amount, String method, String status) throws SQLException;

    /**
     * Finds a payment by payment_id.
     *
     * @param paymentId payment ID
     * @return Map representing the payment record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long paymentId) throws SQLException;

    /**
     * Finds all payments for a loan.
     *
     * @param loanId loan ID
     * @return list of payment records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByLoanId(long loanId) throws SQLException;

    /**
     * Retrieves all loan payments in the system.
     *
     * @return list of payment records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates payment status.
     *
     * @param paymentId payment ID
     * @param status    new status ('success','failed','pending')
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateStatus(long paymentId, String status) throws SQLException;

    /**
     * Deletes a loan payment record.
     *
     * @param paymentId payment ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long paymentId) throws SQLException;
}
