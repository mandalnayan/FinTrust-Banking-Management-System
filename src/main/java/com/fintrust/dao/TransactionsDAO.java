package com.fintrust.dao;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DAO interface for managing bank transactions in the banking system.
 * <p>
 * All operations follow banking-grade secure JDBC standards.
 */
public interface TransactionsDAO {

    /**
     * Records a new transaction.
     *
     * @param accountId        ID of the account
     * @param relatedAccountId related account ID (if any)
     * @param beneficiaryId    beneficiary ID (if any)
     * @param txnReference     unique transaction reference
     * @param txnType          'credit' or 'debit'
     * @param mode             transaction mode ('online','upi','card','neft','rtgs','imps','cash')
     * @param amount           transaction amount
     * @param balanceAfter     balance after transaction
     * @param description      transaction description
     * @param status           transaction status ('pending','completed','failed','reversed')
     * @return generated transaction_id
     * @throws SQLException if database operation fails
     */
    long create(long accountId, Long relatedAccountId, Long beneficiaryId,
                String txnReference, String txnType, String mode,
                double amount, double balanceAfter, String description,
                String status) throws SQLException;

    /**
     * Finds a transaction by transaction_id.
     *
     * @param transactionId primary key
     * @return Map representing the transaction record, or null
     * @throws SQLException if database operation fails
     */
    Map<String, Object> findById(long transactionId) throws SQLException;

    /**
     * Finds all transactions of an account.
     *
     * @param accountId account ID
     * @return list of transaction records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByAccountId(long accountId) throws SQLException;

    /**
     * Retrieves all transactions.
     *
     * @return list of all transaction records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates transaction status.
     *
     * @param transactionId transaction ID
     * @param status        new status ('pending','completed','failed','reversed')
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateStatus(long transactionId, String status) throws SQLException;

    /**
     * Deletes a transaction record.
     *
     * @param transactionId transaction ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long transactionId) throws SQLException;
}
