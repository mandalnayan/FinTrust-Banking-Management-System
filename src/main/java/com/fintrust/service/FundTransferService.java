package com.fintrust.service;

import java.rmi.ServerError;
import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.UserDAO;
import com.fintrust.dao.impl.AccountDAOImpl;
import com.fintrust.dao.impl.FundTransferDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Transaction_copy.TransactionStatus;
import com.fintrust.model.User;
import com.fintrust.util.NotificationUtil;

/**
 * Service layer responsible for handling fund transfers.
 * <p>
 * Ensures transactional integrity, validates KYC, balance, account status,
 * IFSC correctness, and records debit/credit transactions atomically.
 */
public class FundTransferService {

    private static final Logger logger = LogManager.getLogger(FundTransferService.class);

    private Connection connection = DBConnection.getConnection();
    private final FundTransferDAO dao = new FundTransferDAO(connection);
    private final AccountDAO accountDao = new AccountDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl(connection);

    /** Cached sender balance for transaction audit */
    private double senderBalance = 0;

    /**
     * Transfers funds from one account to another.
     *
     * @param fromAcc sender account number
     * @param toAcc receiver account number
     * @param ifscCode receiver IFSC code
     * @param amount transfer amount
     * @return true if transfer is successful, false otherwise
     */
    public boolean transferFunds(Long fromAcc, Long toAcc, String ifscCode, double amount) {

        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
        if (userId == null) {
            logger.error("Fund transfer attempted without logged-in user.");
            throw new IllegalStateException("User not logged in");
        }

        logger.info("Initiating fund transfer. userId={}, fromAcc={}, toAcc={}, amount={}",
                userId, fromAcc, toAcc, amount);

        try {
            // All operations must succeed or fail together
            connection.setAutoCommit(false);

            validateUserKyc(userId);
            validateBalance(fromAcc, amount);
            validateReceiver(toAcc, ifscCode);

            if (!dao.isAccountActive(fromAcc)) {
                throw new IllegalArgumentException("Sender account is not active. Please contact bank!");
            }

            if (!dao.isAccountActive(toAcc)) {
                throw new IllegalArgumentException("Receiver account is not active or bank server is down!");
            }

            dao.debit(fromAcc, amount);
            dao.credit(toAcc, amount);

            if (senderBalance <= 0) {
                throw new IllegalArgumentException("Insufficient balance");
            }

            double remainingBalance = senderBalance - amount;

            // Sender transaction
            dao.insertTransaction(
                    userId,
                    fromAcc,
                    toAcc,
                    amount,
                    "debit",
                    remainingBalance,
                    TransactionStatus.COMPLETED.name().toLowerCase()
            );

            // Receiver transaction
            Long toUserId = accountDao.findUserIdByAccountNo(toAcc);
            double receiverRemainingBalance = dao.getAccountBalance(toAcc) + amount;

            dao.insertTransaction(
                    toUserId,
                    fromAcc,
                    toAcc,
                    amount,
                    "credit",
                    receiverRemainingBalance,
                    TransactionStatus.COMPLETED.name().toLowerCase()
            );

            connection.commit();
            logger.info("Fund transfer completed successfully. fromAcc={}, toAcc={}, amount={}",
                    fromAcc, toAcc, amount);

            return true;

        } catch (Exception e) {

            rollbackQuietly();
            logger.error("Fund transfer failed. fromAcc={}, toAcc={}, amount={}",
                    fromAcc, toAcc, amount, e);

            NotificationUtil.showInstant(
                    "error",
                    "Failed to transfer! " + e.getMessage(),
                    6000
            );

            try {
                if (connection != null) {
                    dao.insertTransaction(
                            userId,
                            fromAcc,
                            toAcc,
                            amount,
                            "debit",
                            senderBalance,
                            TransactionStatus.FAILED.name().toLowerCase()
                    );
                }
            } catch (Exception ex) {
                logger.warn("Failed to record FAILED transaction entry.", ex);
            }

            return false;

        } finally {
            closeQuietly();
        }
    }

    /**
     * Validates whether the user's KYC status allows transactions.
     */
    private void validateUserKyc(Long userId) throws Exception {

        String kycStatus = userDAO.getUserKycStatus(userId);
        if (kycStatus == null) {
            logger.error("KYC status not found for userId={}", userId);
            throw new ServerError("Internal server error!", null);
        }

        boolean isUpdated = kycStatus.equals(User.KycStatus.UPDATED.name());
        if (!isUpdated) {
            logger.warn("Transaction blocked due to KYC status={} for userId={}", kycStatus, userId);
            throw new IllegalArgumentException(
                    "Your KYC status is " + kycStatus + ".\nPlease update KYC to proceed."
            );
        }
    }

    /**
     * Validates sufficient balance in sender account.
     */
    private void validateBalance(Long fromAcc, double amount) throws Exception {

        senderBalance = dao.getAccountBalance(fromAcc);
        if (senderBalance < amount) {
            logger.warn("Insufficient balance. account={}, balance={}, amount={}",
                    fromAcc, senderBalance, amount);
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    /**
     * Validates receiver account IFSC.
     */
    private void validateReceiver(Long toAcc, String ifsc) throws Exception {

        long branchId = dao.getBranchId(toAcc);
        boolean valid = dao.validateIFSC(branchId, ifsc);

        if (!valid) {
            logger.warn("Invalid IFSC provided. toAcc={}, ifsc={}", toAcc, ifsc);
            throw new IllegalArgumentException("Invalid receiver IFSC");
        }
    }

    /**
     * Rolls back transaction safely without throwing further exceptions.
     */
    private void rollbackQuietly() {
        try {
            if (connection != null) {
                connection.rollback();
                logger.debug("Transaction rolled back successfully.");
            }
        } catch (Exception ex) {
            logger.error("Rollback failed.", ex);
        }
    }

    /**
     * Closes JDBC connection safely.
     */
    private void closeQuietly() {
        try {
            if (connection != null) {
                connection.close();
                logger.debug("Database connection closed.");
            }
        } catch (Exception ex) {
            logger.error("Failed to close database connection.", ex);
        }
    }
}
