package com.fintrust.service;

import java.util.List;

import com.fintrust.model.Account;
import com.fintrust.model.Notification;

public interface AccountService {
	// Account management
    Notification openAccount(Account account);
    boolean closeAccount(long accountNo);
    boolean updateAccountDetails(Account account);
    Account getAccountDetails(long accountNo);
    Account getAccountById(long accountId);
    List<Account> getAllAccounts();
    public List<Long> getAllAccountsNumber(); 
    
    boolean isAccountExists(long userId, String accountType);

    // Banking transactions
    boolean deposit(long accountNo, double amount);
    boolean withdraw(long accountNo, double amount);
    double checkBalance(long accountNo);
    boolean transfer(long fromAccountNo, long toAccountNo, double amount);
}
