package com.fintrust.service;

import java.util.List;

import com.fintrust.model.Account;
import com.fintrust.model.Notification;
import com.fintrust.model.Account.AccountType;

public interface AccountService {
	// Account management
    Notification openAccount(Account account);
    boolean closeAccount(long accountNo);
    boolean updateAccountDetails(Account account);
    Account getAccountDetails(long accountNo);
    Account getAccountById(long accountId);
    List<Account> getAllAccounts();
    public List<Long> getAllAccountsNumber(); 
    public List<AccountType> getAllAccountType();
    
    boolean isAccountExists(long user_id, String accountType);

    // Banking transactions
    boolean deposit(long accountNo, double amount);
    boolean withdraw(long accountNo, double amount);
    double checkBalance(long accountNo);
    boolean transfer(long fromAccountNo, long toAccountNo, double amount);
}
