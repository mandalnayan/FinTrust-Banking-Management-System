package com.fintrust.dao.impl;

import java.sql.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fintrust.db.DBConnection;
import com.fintrust.util.NotificationUtil;

/**
 * DAO class responsible for handling fund transfer–related database operations.
 * <p>
 * This class performs balance checks, account validation, debit/credit operations,
 * and transaction logging. All operations use PreparedStatements to comply with
 * banking security standards.
 */
public class FundTransferDAO {

    private static final Logger logger = LogManager.getLogger(FundTransferDAO.class);

    /** JDBC connection managed externally (service layer / transaction scope) */
    private Connection connection;

    /**
     * Constructor for dependency injection.
     *
     * @param conn active JDBC connection
     */
    public FundTransferDAO(Connection conn) {
        this.connection = conn;
        logger.debug("FundTransferDAO initialized with JDBC connection.");
    }

    /**
     * Retrieves the current balance of an account.
     *
     * @param accountNumber account number
     * @return current account balance
     * @throws SQLException if account is not found or query fails
     */
    public double getAccountBalance(Long accountNumber) throws SQLException {

        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Account not found while fetching balance. accountNumber={}", accountNumber);
                    throw new SQLException("Account not found: " + accountNumber);
                }
                double balance = rs.getDouble("balance");
                logger.debug("Fetched balance for accountNumber={}", accountNumber);
                return balance;
            }
        }
    }

    /**
     * Retrieves branch ID associated with an account.
     *
     * @param accountNumber account number
     * @return branch ID
     * @throws SQLException if account is not found
     */
    public long getBranchId(Long accountNumber) throws SQLException {

        String sql = "SELECT branch_id FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    logger.warn("Receiver account not found while fetching branchId. accountNumber={}", accountNumber);
                    throw new SQLException("Receiver account not found");
                }
                return rs.getLong("branch_id");
            }
        }
    }

    /**
     * Validates IFSC code against a branch ID.
     *
     * @param branchId branch ID
     * @param ifsc IFSC code
     * @return {@code true} if IFSC matches branch
     * @throws SQLException if query fails
     */
    public boolean validateIFSC(long branchId, String ifsc) throws SQLException {

        String sql = "SELECT 1 FROM branches WHERE branch_id = ? AND ifsc_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, branchId);
            ps.setString(2, ifsc);
            try (ResultSet rs = ps.executeQuery()) {
                boolean valid = rs.next();
                logger.debug("IFSC validation result={} for branchId={}", valid, branchId);
                return valid;
            }
        }
    }

    /**
     * Debits amount from an active account.
     *
     * @param accountNumber account number
     * @param amount debit amount
     * @return number of affected rows
     * @throws SQLException if update fails
     */
    public int debit(Long accountNumber, double amount) throws SQLException {

        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);
            int rows = ps.executeUpdate();
            logger.info("Debit executed. accountNumber={}, amount={}, rowsAffected={}",
                    accountNumber, amount, rows);
            return rows;
        }
    }

    /**
     * Credits amount to an active account.
     *
     * @param accountNumber account number
     * @param amount credit amount
     * @return number of affected rows
     * @throws SQLException if update fails
     */
    public int credit(Long accountNumber, double amount) throws SQLException {

        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setLong(2, accountNumber);
            int resValue = ps.executeUpdate();
            logger.info("Credit executed. accountNumber={}, amount={}, rowsAffected={}",
                    accountNumber, amount, resValue);
            return resValue;
        }
    }

    /**
     * Checks whether an account is active.
     *
     * @param accountNumber account number
     * @return {@code true} if account is active
     * @throws SQLException if query fails
     */
    public boolean isAccountActive(Long accountNumber) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ? and status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                boolean active = rs.next();
                logger.debug("Account active check result={} for accountNumber={}", active, accountNumber);
                return active;
            }
        }
    }

    /**
     * Inserts a transaction record into the transactions table.
     *
     * @param userId user ID
     * @param fromAcc sender account
     * @param toAcc receiver account
     * @param amount transaction amount
     * @param txn_type transaction type
     * @param remaningBalance balance after transaction
     * @param status transaction status
     * @throws SQLException if insert fails
     */
    public void insertTransaction(Long userId,
                                  Long fromAcc,
                                  Long toAcc,
                                  double amount,
                                  String txn_type,
                                  double remaningBalance,
                                  String status) throws SQLException {

        String sql = """
            INSERT INTO transactions
            (user_id, account_number, counterparty_account_number, amount, balance_after, txn_type, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, fromAcc);
            ps.setLong(3, toAcc);
            ps.setDouble(4, amount);
            ps.setDouble(5, remaningBalance);
            ps.setString(6, txn_type);
            ps.setString(7, status);
            ps.executeUpdate();

            logger.info(
                "Transaction recorded. userId={}, fromAcc={}, toAcc={}, amount={}, status={}",
                userId, fromAcc, toAcc, amount, status
            );
        }
    }
}
