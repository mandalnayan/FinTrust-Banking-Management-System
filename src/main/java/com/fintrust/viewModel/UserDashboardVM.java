package com.fintrust.viewModel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Include;

import com.fintrust.dao.impl.TransactionDAO;
import com.fintrust.dao.impl.TransactionsDAOImpl;
import com.fintrust.db.DBConnection;
import com.fintrust.model.Account;
import com.fintrust.model.Transaction;
import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.CardServices;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.util.NotificationUtil;

import java.sql.SQLException;
import java.util.List;

public class UserDashboardVM {

	private AccountService accountService;
	private UserDetailsServiceImpl userDetailsServiceImpl;
	private TransactionsDAOImpl transactionDAO = new TransactionsDAOImpl(DBConnection.getConnection());
	private CardServices cardService = new CardServices();

	private Account selectedAccount;
	private Long selectedAccountNo;
	private int pendingCount;
	private int rewardPoints;
	private Long activeCards;
	private double availableBalance;
	private String transferAmount;
	private User user;
	private String kycStatus;

	private List<Transaction> recentTransactions;
	private List<Account> accountList;

	// ==========================
	// INITIALIZATION
	// ==========================
	@Init
	public void init() {
		accountService = new AccountServiceImpl();
		userDetailsServiceImpl = new UserDetailsServiceImpl();
		Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
		if (userId == null) {
			return;
		}

		Long accountId = userDetailsServiceImpl.getPrimaryAccount(userId);
		user = userDetailsServiceImpl.getLogedInUser();
		kycStatus = user.getKycStatus().name().toUpperCase();
		
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
		activeCards = cardService.getActiveCardCount();

		// Load sample transactions
		try {
			recentTransactions = transactionDAO.allCurrentUserTransactions(null, null);
		} catch (SQLException e) {
			NotificationUtil.showInstant("error", "Failed to load transaction");
			e.printStackTrace();
		}
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

	public Long getActiveCards() {
		return activeCards;
	}

	public String getKycLabel() {
	    if (kycStatus == null) {
	        return "PENDING ⚠️";
	    }

	    switch (kycStatus) {
	        case "UPDATED":
	            return "UPDATED 🎉";
	        case "REQUESTED":
	            return "REQUESTED ⏳";
	        case "REJECTED":
	            return "REJECTED ❌";
	        case "PENDING":
	        default:
	            return "PENDING ⚠️";
	    }
	}


	public String getKycStyle() {
	    return "UPDATED".equals(kycStatus)
	        ? "font-weight:bold;color:green;"
	        : "font-weight:bold;color:red;";
	}


	public String getFormattedAmount(Transaction t) {
		return (t.getTxnType().equalsIgnoreCase("credit") ? "+" : "-") + String.format("%.2f", t.getAmount()) + "₹";
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

	@Command
	public void transactionHistory(@BindingParam("page") String url) {
		Include mainContentSec = (Include) Sessions.getCurrent().getAttribute("main_content_sec");

		mainContentSec.setSrc("/WEB-INF/components/" + url + ".zul");

	}
	
	@Command
	public void tranfer(@BindingParam("page") String url) {
		Include mainContentSec = (Include) Sessions.getCurrent().getAttribute("main_content_sec");

		mainContentSec.setSrc("/WEB-INF/components/" + url + ".zul");

	}
	
	@Command
	public void kycUpdate(@BindingParam("page") String url) {
		Include mainContentSec = (Include) Sessions.getCurrent().getAttribute("main_content_sec");

		mainContentSec.setSrc("/WEB-INF/components/" + url + ".zul");

	}

}
