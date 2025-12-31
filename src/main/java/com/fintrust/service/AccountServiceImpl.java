package com.fintrust.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zkoss.zk.ui.Sessions;

import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;
import com.fintrust.model.Notification;
import com.fintrust.util.NotificationUtil;
import com.fintrust.dao.impl.AccountDAOImpl;
import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.dao.AccountDAO;
import com.fintrust.dao.UserDetailsDAO;

public class AccountServiceImpl implements AccountService {

	private final AccountDAO accountDAO;
	private final UserDetailsDAO userDetailsDAO;
	private Connection connection = DBConnection.getConnection();
	
	private static final String LOGIN_USER = "user_id";
	

	public AccountServiceImpl() {
		this.accountDAO = new AccountDAOImpl(connection);
		this.userDetailsDAO = new UserDetailsDAOImpl(connection);
	}

	@Override
	public boolean isAccountExists(long userId, String accountType) {
		try {
			return accountDAO.findByType(userId, accountType) != null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Notification openAccount(Account account) {
		try {
			// Unique account number generation (for demo)
			connection.setAutoCommit(false);
			long accountNo = generateAccountNumber();

			Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");

			account.setUserId(userId);

			if (accountNo == -1 || userId == null) {
				return new Notification("error", "Server error. \nPlease try again!!");
			} else if(isAccountExists(userId, account.getAccountType().toString())) {
				return new Notification("warning", "Same type of account already exist. \n Sorry you can't create same account!!");
			}	
						
			account.setAccountNumber(accountNo);
			long account_id = accountDAO.create(account);
			List<Account> accounts = accountDAO.findByUserId(userId);
			if (accounts.size() == 1) {
				userDetailsDAO.updatePrimaryAccount(userId, account_id);
			}
			connection.commit();
			return new Notification("info", "Account created successfully!!");
		} catch (SQLException e) {
			try {
				connection.rollback(); // Roll back if either any (account creation / primary account updation) fail
			} catch (SQLException e1) {

				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return new Notification("error", "Server error. \nPlease try again!!");
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
	public Account getAccountDetails(long accountNo) {
		try {
			return accountDAO.findByNumber(accountNo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Account getAccountById(long accountId) {
		try {
			return accountDAO.findById(accountId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Account> getAllAccounts() {

		List<Account> accounts = new ArrayList<>();

		try {
			accounts = accountDAO.findAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}

	/**
	 * Find all created accounts
	 */
	@Override
	public List<Account> getAllUserAccounts() {
		long userId = (long) Sessions.getCurrent().getAttribute("user_id");
		List<Account> accounts = new ArrayList<>();
		try {
			accounts = accountDAO.findByUserId(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}
	
	@Override
	public List<Long> getAllAccountsNumber() {
		long userId = (long) Sessions.getCurrent().getAttribute("user_id");
		List<Long> accounts = new ArrayList<>();
		try {
			accounts = accountDAO.findByNumberUserId(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}

	@Override
	public boolean deposit(long accountNo, double amount) {
		if (amount <= 0)
			return false;

		try {
			Account acc = accountDAO.findById(accountNo);
			if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE)
				return false;

			double newBalance = acc.getBalance() + amount;
			return accountDAO.updateBalance(accountNo, newBalance);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean withdraw(long accountNo, double amount) {
		if (amount <= 0)
			return false;

		Account acc = null;
		try {
			acc = accountDAO.findById(accountNo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE)
			return false;
		if (acc.getBalance() < amount)
			return false;

		double newBalance = acc.getBalance() - amount;
		try {
			return accountDAO.updateBalance(accountNo, newBalance);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public double checkBalance(long accountNo) {
		Account acc = null;
		try {
			acc = accountDAO.findByNumber(accountNo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (acc != null) ? acc.getBalance() : 0.0;
	}

	@Override
	public boolean transfer(long fromAccountNo, long toAccountNo, double amount) {
		if (amount <= 0)
			return false;

		Account fromAcc = null, toAcc = null;

		try {
			fromAcc = accountDAO.findById(fromAccountNo);
			toAcc = accountDAO.findById(toAccountNo);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (fromAcc == null || toAcc == null)
			return false;
		if (fromAcc.getBalance() < amount)
			return false;

		double newFromBal = fromAcc.getBalance() - amount;
		double newToBal = toAcc.getBalance() + amount;

		try {
			return accountDAO.updateBalance(fromAccountNo, newFromBal)
					&& accountDAO.updateBalance(toAccountNo, newToBal);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private long generateAccountNumber() {
		long highest_accountNo = new AccountDAOImpl().getHighestAccountNo();
		if (highest_accountNo == 0) {
			return generateRandomNumber();
		}
		return highest_accountNo != -1 ? highest_accountNo + 1 : -1;
	}

	private long generateRandomNumber() {
		Random random = new Random();
		long min = 100000000000L;
		long max = 999999999999L;
		
		while (min++ < max) {
			try {
				if (accountDAO.findByNumber(min) == null) {
					return min;
				}
			} catch (SQLException e) {
				System.out.println("ERROR to check account is present or not: " + e.getMessage());
				e.printStackTrace();
				return -1l;
			}
		}
		return -1l;
	}

}
