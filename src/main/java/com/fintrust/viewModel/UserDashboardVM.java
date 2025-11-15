package com.fintrust.viewModel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;

import com.fintrust.dao.AccountDao;
import com.fintrust.dao.AccountDaoImp;
import com.fintrust.model.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardVM {

    private double availableBalance;
    private Long defaultAccountNo;
    private int pendingCount;
    private int rewardPoints;
    private int activeCards;
    private Account defaultAccount;
    
    private AccountDao accountDao;

    private List<Transaction> recentTransactions;

    // ==========================
    // GETTERS (required for MVVM)
    // ==========================
    public Double getAvailableBalance() {
        return availableBalance;
    }

    public Long getDefaultAccountNo() {
        return defaultAccountNo;
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
    // INITIALIZATION
    // ==========================
    @Init
    public void init() {
    	
    	accountDao = new AccountDaoImp();

        // TODO → Replace these with service/database calls
        defaultAccount = accountDao.getDefaultAccount();
        defaultAccountNo = defaultAccount.getAccount_no();
        availableBalance = defaultAccount.getBalance();
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



