package com.fintrust.viewModel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.dao.impl.TransactionDAO;
import com.fintrust.model.Account;
import com.fintrust.model.Transaction;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.UserDetailsServiceImpl;

import java.util.List;

public class UserDashboardVM {

	private AccountService accountService;
	private UserDetailsServiceImpl userDetailsServiceImpl;
	private TransactionDAO transactionDAO = new TransactionDAO();
	
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
		recentTransactions = transactionDAO.getTransactions(userId, null, null);				
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

	}
