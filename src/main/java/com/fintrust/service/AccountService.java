package com.fintrust.service;

import java.util.List;

import com.fintrust.model.Account;

public interface AccountService {
	// Account management
    boolean openAccount(Account account);
    boolean closeAccount(long accountNo);
    boolean updateAccountDetails(Account account);
    Account getAccountDetails(long accountNo);
    List<Account> listAllAccounts();
    List<Account> listAllAccounts(long customer_id);
    
    boolean isAccountExists(long customer_id, String accountType);

    // Banking transactions
    boolean deposit(long accountNo, double amount);
    boolean withdraw(long accountNo, double amount);
    double checkBalance(long accountNo);
    boolean transfer(long fromAccountNo, long toAccountNo, double amount);
}
