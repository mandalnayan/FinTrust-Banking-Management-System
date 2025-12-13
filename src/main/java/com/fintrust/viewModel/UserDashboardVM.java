package com.fintrust.viewModel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import com.fintrust.dao.impl.UserDetailsDAOImpl;
import com.fintrust.model.Account;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.UserDetailsServiceImpl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardVM {

	private AccountService accountService;
	UserDetailsServiceImpl userDetailsServiceImpl;
	private Account selectedAccount;
	private Long selectedAccountNo;
	private int pendingCount;
	private int rewardPoints;
	private int activeCards;
	private double availableBalance;

	private List<Transaction> recentTransactions;
	private List<Account> accountList;

	// ==========================
	// INITIALIZATION
	// ==========================
	@Init	
	public void init() {	
		accountService  = new AccountServiceImpl();
		userDetailsServiceImpl = new UserDetailsServiceImpl();		
		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		if (userId == null) {
			return;
		}

		Long accountId = userDetailsServiceImpl.getPrimaryAccount(userId);
		if (accountId == -1) {
			return;
		}
		
		selectedAccount = accountService.getAccountById(accountId);
		if (selectedAccount == null)
			return;
		availableBalance = selectedAccount.getBalance();
		selectedAccountNo = selectedAccount.getAccountNumber();
		pendingCount = 5;
		rewardPoints = 125;
		activeCards = 2;

		// Load sample transactions
		recentTransactions = new ArrayList<>();
		recentTransactions
				.add(new Transaction("2025-11-08", "POS - Grocery Store", "Debit", "-₹ 3,250.00", "Completed"));
	}

	// ==========================
	// GETTERS (required for MVVM)
	// ==========================

	// Return accountlist
	public List<Account> getAccountList() {
		return accountList;
	}

	// This dynamically returns balance of the selected account
	
	public Double getAvailableBalance() {		
		return availableBalance;
	}

	public Long getSelectedAccountNo() {		
		return selectedAccountNo;
	}

	public int getPendingCount() {
		return pendingCount;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}

	public int getActiveCards() {
		return activeCards;
	}

	public List<Transaction> getRecentTransactions() {
		return recentTransactions;
	}

	// ==========================
	// COMMAND: Navigation
	// ==========================
	@Command
	public void go(@BindingParam("page") String page) {
		Executions.sendRedirect("/user/" + page + ".zul");
	}

	// ==========================
	// Inner class representing a transaction
	// ==========================
	public static class Transaction {
		private String date;
		private String description;
		private String type;
		private String amount;
		private String status;

		public Transaction(String date, String description, String type, String amount, String status) {
			this.date = date;
			this.description = description;
			this.type = type;
			this.amount = amount;
			this.status = status;
		}

		public String getDate() {
			return date;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}

		public String getAmount() {
			return amount;
		}

		public String getStatus() {
			return status;
		}
	}
}
