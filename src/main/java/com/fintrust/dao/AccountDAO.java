package com.fintrust.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fintrust.model.Account;

/**
 * DAO interface for managing bank accounts in the banking system.
 * <p>
 * All operations follow banking-grade JDBC standards.
 */
public interface AccountDAO {

    /**
     * Creates a new account.
     *
     * @param userId       ID of the user owning the account
     * @param bankId       ID of the bank
     * @param accountNumber unique account number
     * @param accountType  'savings','current','salary','fixed_deposit'
     * @param balance      initial balance
     * @param currency     currency code (e.g., 'INR')
     * @param status       'active','inactive','frozen','closed'
     * @return generated account_id
     * @throws SQLException if database operation fails
     */
    long create(Account account) throws SQLException;

    /**
     * Finds an account by account_id.
     *
     * @param accountId primary key
     * @return Map representing the account record or null
     * @throws SQLException if database operation fails
     */
    Account findById(long accountId) throws SQLException;
    
    /**
     * Finds an account by account_id.
     *
     * @param accountId primary key
     * @return Map representing the account record or null
     * @throws SQLException if database operation fails
     */
    Account findByNumber(long accountNo) throws SQLException;

    /**
     * Finds accounts by user_id.
     *
     * @param userId user ID
     * @return list of account records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findByUserId(long userId) throws SQLException;
    
    Account findByType(long userId, String type) throws SQLException;

    /**
     * Retrieves all accounts.
     *
     * @return list of account records
     * @throws SQLException if database operation fails
     */
    List<Map<String, Object>> findAll() throws SQLException;

    /**
     * Updates account information (excluding account number and balance).
     *
     * @param accountId   account ID
     * @param accountType type of account
     * @param status      account status
     * @param currency    currency
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean update(long accountId, String accountType, String status, String currency) throws SQLException;

    /**
     * Updates account balance.
     *
     * @param accountId account ID
     * @param balance   new balance
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateBalance(long accountId, double balance) throws SQLException;

    /**
     * Deletes an account record.
     *
     * @param accountId account ID
     * @return true if delete successful
     * @throws SQLException if database operation fails
     */
    boolean delete(long accountId) throws SQLException;
}

