package com.fintrust.viewModel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.model.Account;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.UserDetailsServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardVM {

	private Account selectedAccount;
    private int pendingCount;
    private int rewardPoints;
    private int activeCards;
    private double availableBalance;
    private AccountService accountService;

    private List<Transaction> recentTransactions;
    private List<Account> accountList;
    

    // ==========================
    // INITIALIZATION
    // ==========================
    @Init
    public void init() {
    	
    	accountService = new AccountServiceImpl();

        // TODO → Replace these with service/database calls
    	
    	accountList = accountService.getAllAccounts();
        if (accountList == null || accountList.size() == 0) {
        	System.out.println("Failed to load accounts or Accounts doesn't exist ..!");
        	return;
        }
        UserDetailsServiceImpl userDetailsServiceImpl = new UserDetailsServiceImpl();
        Long userId = (Long) Sessions.getCurrent().getAttribute("user_id");
        if (userId == null) {
        	return;
        }
       Long accountId = userDetailsServiceImpl.getPrimaryAccount(userId);
       if (accountId == -1) {
         Executions.sendRedirect("/user/userDashboard.zul");
       }
        selectedAccount = accountList.get(0);
        availableBalance = selectedAccount.getBalance();
        pendingCount = 5;
        rewardPoints = 125;
        activeCards = 2;

        // Load sample transactions
        recentTransactions = new ArrayList<>();
        recentTransactions.add(new Transaction(
                "2025-11-08",
                "POS - Grocery Store",
                "Debit",
                "-₹ 3,250.00",
                "Completed"
        ));
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
        if (selectedAccount != null) {
            return selectedAccount.getBalance(); // adjust type accordingly
        }
        return null;
    }
    
    @NotifyChange({"selectedAccount", "availableBalance"})
    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
        if (selectedAccount != null) {        	
            this.availableBalance = selectedAccount.getBalance();
        }
    }
    
    public Account getSelectedAccount() {
        return selectedAccount;
    }
    
	public Long getSelectedAccountNo() {
		if (selectedAccount != null) {
			selectedAccount.getAccountNumber();
		}
		return null;
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



