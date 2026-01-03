package com.fintrust.service;

import java.util.List;

import com.fintrust.model.Account;
import com.fintrust.model.Notification;

/**
 * Service interface that defines all business operations related to
 * bank account management and transactions.
 * <p>
 * Implementations of this interface handle account lifecycle operations
 * such as opening, updating, closing accounts, as well as core banking
 * transactions like deposits, withdrawals, and transfers.
 * </p>
 *
 * @author FinTrust
 */
public interface AccountService {
//	// Account management
//    Notification openAccount(Account account);
//    boolean closeAccount(long accountNo);
//    boolean updateAccountDetails(Account account);
//    Account getAccountDetails(long accountNo);
//    Account getAccountById(long accountId);
//    List<Account> getAllAccounts();
//    public List<Long> getAllAccountsNumber(); 
//    
//    boolean isAccountExists(long userId, String accountType);


    /* ===================== Account Management ===================== */

    /**
     * Opens a new bank account for a user.
     *
     * @param account the {@link Account} object containing account details
     * @return a {@link Notification} indicating success or failure with message
     */
    Notification openAccount(Account account);

    /**
     * Closes an existing bank account.
     *
     * @param accountNo the account number to be closed
     * @return {@code true} if the account was closed successfully,
     *         {@code false} otherwise
     */
    boolean closeAccount(long accountNo);

    /**
     * Updates the details of an existing account.
     *
     * @param account the {@link Account} object containing updated information
     * @return {@code true} if the update was successful, {@code false} otherwise
     */
    boolean updateAccountDetails(Account account);

    /**
     * Retrieves account details using the account number.
     *
     * @param accountNo the account number
     * @return the {@link Account} if found, otherwise {@code null}
     */
    Account getAccountDetails(long accountNo);

    /**
     * Retrieves account details using the account ID.
     *
     * @param accountId the unique account ID
     * @return the {@link Account} if found, otherwise {@code null}
     */
    Account getAccountById(long accountId);

    /**
     * Retrieves all accounts in the system.
     *
     * @return a list of all {@link Account} records
     */
    List<Account> getAllAccounts();

    /**
     * Retrieves all accounts belonging to the currently logged-in user.
     *
     * @return a list of {@link Account} objects owned by the user
     */
    List<Account> getAllUserAccounts();

    /**
     * Retrieves all account numbers in the system.
     *
     * @return a list of account numbers
     */
    List<Long> getAllAccountsNumber();

    /**
     * Checks whether an account already exists for a user with a given account type.
     *
     * @param userId the user ID
     * @param accountType the account type (e.g., SAVINGS, CURRENT)
     * @return {@code true} if the account exists, {@code false} otherwise
     */
    boolean isAccountExists(long userId, String accountType);

    /* ===================== Banking Transactions ===================== */

    /**
     * Deposits a specified amount into an account.
     *
     * @param accountNo the account number
     * @param amount the amount to deposit (must be positive)
     * @return {@code true} if the deposit was successful, {@code false} otherwise
     */
    boolean deposit(long accountNo, double amount);

    /**
     * Withdraws a specified amount from an account.
     *
     * @param accountNo the account number
     * @param amount the amount to withdraw (must be positive and within balance)
     * @return {@code true} if the withdrawal was successful, {@code false} otherwise
     */
    boolean withdraw(long accountNo, double amount);

    /**
     * Checks the current balance of an account.
     *
     * @param accountNo the account number
     * @return the current account balance
     */
    double checkBalance(long accountNo);

    /**
     * Transfers a specified amount from one account to another.
     *
     * @param fromAccountNo the source account number
     * @param toAccountNo the destination account number
     * @param amount the amount to transfer (must be positive)
     * @return {@code true} if the transfer was successful, {@code false} otherwise
     */
    boolean transfer(long fromAccountNo, long toAccountNo, double amount);
}
