package com.fintrust.viewModel;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.chart.Charts;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.util.List;
import java.sql.SQLException;
import java.util.Arrays;

import com.fintrust.dao.impl.AccountCloseRequestDao;
import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.dao.impl.CardsDAOImpl;
import com.fintrust.dao.impl.TransactionDAO;
import com.fintrust.model.Transaction_copy;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.TransactionService;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class AdminDashboardVM {

    private long totalUsers;
    private long totalAccounts;
    private double todayTransactions;
    private long pendingApprovals;

    private List<String> alerts;
    private List<Transaction_copy> recentTransactions;

    private UserService userService;
    private AccountService accountService;
    private TransactionService transactionService;
    private CategoryModel transactionChartModel;

    private TransactionDAO transactionDAO = new TransactionDAO();

    
    @Init
    public void init() throws SQLException {

        accountService = new AccountServiceImpl();
        userService = new UserServiceImpl();
        transactionService = new TransactionService();
        
        totalUsers = userService.getTotalUsers();
        totalAccounts = accountService.getAllAccounts().size();
        todayTransactions = transactionService.getTodayTotalTransaction();
        Long numberOfUpdateAccountPendingRequest =
                new AccountUpdateRequestDao().getNumberOfPendingRequest();
        Long numberOfCloseAccountPendingRequest =
                new AccountCloseRequestDao().getNumberOfPendingRequest();
        Long numberOfPendingCardRequest =
                new CardsDAOImpl().getNumberOfPendingRequest();
        Long numberOfPendingKycRequest =
        		userService.getNumberOfPendingKycRequest();

        pendingApprovals =  (
                numberOfUpdateAccountPendingRequest
                        + numberOfCloseAccountPendingRequest
                        + numberOfPendingCardRequest
                        + numberOfPendingKycRequest
        );

    }
    
    // ===============================
    // Getters
    // ===============================
    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public double getTodayTransactions() {
        return todayTransactions;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public List<Transaction_copy> getRecentTransactions() {
        return recentTransactions;
    }

    public CategoryModel getTransactionChartModel() {
        return transactionChartModel;
    }
}
