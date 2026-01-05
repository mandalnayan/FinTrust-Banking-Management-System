package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.UserDetailsDAO;
import com.fintrust.dao.impl.AccountDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Notification;

public class AccountServiceImpl implements AccountService {

    /** Logger for this class */
    private static final Logger LOGGER = Logger.getLogger(AccountServiceImpl.class.getName());

    /** Session attribute key for logged-in user */
    private static final String LOGIN_USER = "user_id";

    /** Account number boundaries */
    private static final long ACCOUNT_NO_MIN = 100000000000L;
    private static final long ACCOUNT_NO_MAX = 999999999999L;

    private final AccountDAO accountDAO;
    private final UserDetailsDAO userDetailsDAO;
    private final Connection connection = DBConnection.getConnection();

    public AccountServiceImpl() {
        this.accountDAO = new AccountDAOImpl(connection);
        this.userDetailsDAO = new UserDetailsDAOImpl(connection);
    }

    /**
     * Checks whether an account already exists for a user with a given type.
     * 
     * @param userId - logged-in user ID
     * @param accountType - account type
     * @return true if account exists, false otherwise
     */
    @Override
    public boolean isAccountExists(long userId, String accountType) {
        try {
            return accountDAO.findByType(userId, accountType) != null;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking account existence for userId=" + userId, e);
            return false;
        }
    }

    /**
     * Opens a new bank account for the logged-in user.
     * 
     * @param account - account entity
     * @return Notification -  object indicating success or failure
     */
    @Override
    public Notification openAccount(Account account) {
        try {
            connection.setAutoCommit(false);

            Long userId = getLoggedInUserId();
            if (userId == null) {
                return errorNotification();
            }

            long accountNo = generateAccountNumber();
            if (accountNo == -1) {
                return errorNotification();
            }

            if (isAccountExists(userId, account.getAccountType().toString())) {
                return new Notification("warning",
                        "Same type of account already exists. You cannot create another one.");
            }

            account.setUserId(userId);
            account.setAccountNumber(accountNo);

            long account_id = accountDAO.create(account);
            updatePrimaryAccountIfFirst(userId, account_id);

            connection.commit();
            LOGGER.info("Account created successfully for userId=" + userId);
            return new Notification("info", "Account created successfully!");

        } catch (SQLException e) {
            rollbackTransaction();
            LOGGER.log(Level.SEVERE, "Error opening account", e);
            return errorNotification();
        }
    }

    /**
     * Gets account details by account number.
     *
     * @param accountN - account number
     * @return Account - object or null
     */
    @Override
    public Account getAccountDetails(long accountNo) {
        try {
            return accountDAO.findByNumber(accountNo);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching account details for accountNo=" + accountNo, e);
            return null;
        }
    }

    /**
     * Gets account details by account ID.
     *
     * @param accountId - account ID
     * @return Account - object or null
     */
    @Override
    public Account getAccountById(long accountId) {
        try {
            return accountDAO.findById(accountId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching account by ID=" + accountId, e);
            return null;
        }
    }

    /**
     * Fetches all accounts in the system.
     *
     * @return list of accounts
     */
    @Override
    public List<Account> getAllAccounts() {
        try {
            return accountDAO.findAll();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all accounts", e);
            return new ArrayList<>();
        }
    }

    /**
     * Fetches all accounts for logged-in user.
     *
     * @return list of accounts
     */
    @Override
    public List<Account> getAllUserAccounts() {
        try {
            return accountDAO.findByUserId(getLoggedInUserId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user accounts", e);
            return new ArrayList<>();
        }
    }

    /**
     * Deposits money into an account.
     *
     * @param accountNo account number
     * @param amount    deposit amount
     * @return true if successful
     */
    @Override
    public boolean deposit(long accountNo, double amount) {
        if (amount <= 0) {
            LOGGER.warning("Invalid deposit amount: " + amount);
            return false;
        }

        try {
            Account acc = accountDAO.findById(accountNo);
            if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE) {
                return false;
            }

            return accountDAO.updateBalance(accountNo, acc.getBalance() + amount);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Deposit failed for accountNo=" + accountNo, e);
            return false;
        }
    }

    /**
     * Withdraws money from an account.
     *
     * @param accountNo account number
     * @param amount    withdrawal amount
     * @return true if successful
     */
    @Override
    public boolean withdraw(long accountNo, double amount) {
        if (amount <= 0) {
            return false;
        }

        try {
            Account acc = accountDAO.findById(accountNo);
            if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE || acc.getBalance() < amount) {
                return false;
            }

            return accountDAO.updateBalance(accountNo, acc.getBalance() - amount);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Withdrawal failed for accountNo=" + accountNo, e);
            return false;
        }
    }

    /**
     * Transfers money between two accounts.
     *
     * @param fromAccountNo source account
     * @param toAccountNo   destination account
     * @param amount        transfer amount
     * @return true if successful
     */
    @Override
    public boolean transfer(long fromAccountNo, long toAccountNo, double amount) {

        if (amount <= 0) {
            return false;
        }

        try {
            connection.setAutoCommit(false); // START TRANSACTION

            Account fromAcc = accountDAO.findById(fromAccountNo);
            Account toAcc = accountDAO.findById(toAccountNo);

            if (fromAcc == null || toAcc == null || fromAcc.getBalance() < amount) {
                connection.rollback();
                return false;
            }

            boolean debit = accountDAO.updateBalance(
                    fromAccountNo, fromAcc.getBalance() - amount);

            boolean credit = accountDAO.updateBalance(
                    toAccountNo, toAcc.getBalance() + amount);

            if (debit && credit) {
                connection.commit(); // SUCCESS
                return true;
            } else {
                connection.rollback(); // PARTIAL FAILURE
                return false;
            }

        } catch (SQLException e) {
        	rollbackTransaction();
        	
            LOGGER.log(Level.SEVERE, "Transfer failed", e);
            return false;

        } finally {
            try {
                connection.setAutoCommit(true); // RESTORE DEFAULT
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to reset auto-commit", e);
            }
        }
    }

    /* ================= PRIVATE UTILITY METHODS ================= */

    private Long getLoggedInUserId() {
        return (Long) Sessions.getCurrent().getAttribute(LOGIN_USER);
    }

    private void rollbackTransaction() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Rollback failed", e);
        }
    }

    private Notification errorNotification() {
        return new Notification("error", "Server error. Please try again.");
    }

    private void updatePrimaryAccountIfFirst(long userId, long account_id) throws SQLException {
        List<Account> accounts = accountDAO.findByUserId(userId);
        if (accounts.size() == 1) {
            userDetailsDAO.updatePrimaryAccount(userId, account_id);
        }
    }

    private long generateAccountNumber() {
        long highest_accountNo = new AccountDAOImpl().getHighestAccountNo();
        return highest_accountNo > 0 ? highest_accountNo + 1 : generateRandomNumber();
    }

    private long generateRandomNumber() {
        for (long num = ACCOUNT_NO_MIN; num <= ACCOUNT_NO_MAX; num++) {
            try {
                if (accountDAO.findByNumber(num) == null) {
                    return num;
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error generating account number", e);
                return -1;
            }
        }
        return -1;
    }

	@Override
	public boolean closeAccount(long accountNo) {
		return false;
	}

	@Override
	public boolean updateAccountDetails(Account account) {
		return false;
	}

	@Override
	public List<Long> getAllAccountsNumber() {
		return null;
	}

	@Override
	public double checkBalance(long accountNo) {
		return 0;
	}
}
