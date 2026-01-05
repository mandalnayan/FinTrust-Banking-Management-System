package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Init;
import java.util.List;
import java.sql.SQLException;
import java.util.Arrays;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.impl.AccountCloseRequestDao;
import com.fintrust.dao.impl.AccountUpdateRequestDao;
import com.fintrust.dao.impl.CardsDAOImpl;
import com.fintrust.dao.impl.TransactionDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.model.AccountCloseRequest;
import com.fintrust.model.Transaction_copy;
import com.fintrust.service.AccountService;
import com.fintrust.service.AccountServiceImpl;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class AdminDashboardVM {

    private long totalUsers;
    private long totalAccounts;
    private long todayTransactions;
    private int pendingApprovals;

    private List<String> alerts;
    private List<Transaction_copy> recentTransactions;

    private UserService userService;
    private AccountService accountService;
    
    private TransactionDAO transactionDAO = new TransactionDAO();

    @Init
    public void init() throws SQLException {
    	accountService = new AccountServiceImpl();
    	userService = new UserServiceImpl();    	
    	
        totalUsers = userService.getTotalUsers();
        totalAccounts = accountService.getAllAccounts().size();
        Long numberOfUpdateAccountPendingRequest = new AccountUpdateRequestDao().getNumberOfPendingRequest();
        Long numberOfCloseAccountPendingRequest = new AccountCloseRequestDao().getNumberOfPendingRequest();
        Long numberOfPendingCardRequest = new CardsDAOImpl().getNumberOfPendingRequest();
        
        todayTransactions = 158;
        pendingApprovals = (int) (numberOfUpdateAccountPendingRequest + numberOfCloseAccountPendingRequest + numberOfPendingCardRequest);

        alerts = Arrays.asList(
            "⚠ 5 failed login attempts detected",
            "⚠ High-value transfer pending approval",
            "⚠ Suspicious IP activity detected"
        );
        //recentTransactions = transactionDAO.getRecentTransactionsForAdmin();
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public long getTodayTransactions() {
        return todayTransactions;
    }

    public int getPendingApprovals() {
        return pendingApprovals;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public List<Transaction_copy> getRecentTransactions() {
        return recentTransactions;
    }
}
