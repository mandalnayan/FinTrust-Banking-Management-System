package com.fintrust.service;

import java.util.List;

import com.fintrust.dao.AccountDao;
import com.fintrust.dao.AccountDaoImp;
import com.fintrust.model.Account;
import com.fintrust.model.Account.AccountStatus;

public class AccountServiceImp implements AccountService {

    private final AccountDao accountDAO;

    public AccountServiceImp() {
        this.accountDAO = new AccountDaoImp();
    }
    
    @Override
	public boolean isAccountExists(long customer_id, String accountType) {
		return accountDAO.isSameAccount(customer_id,accountType);
	}

    @Override
    public boolean openAccount(Account account) {
        return accountDAO.createAccount(account);
    }

    @Override
    public boolean closeAccount(long accountNo) {
        return accountDAO.updateAccountStatus(accountNo, AccountStatus.CLOSED.toString());
    }

    @Override
    public boolean updateAccountDetails(Account account) {
        return accountDAO.updateAccount(account);
    }

    @Override
    public Account getAccountDetails(long accountNo) {
        return accountDAO.getAccountByNo(accountNo);
    }

    @Override
    public List<Account> listAllAccounts() {
        return accountDAO.getAllAccounts();
    }
    
	@Override
	public List<Account> listAllAccounts(long customer_id) {
		 return accountDAO.getAllAccounts(customer_id);
	}

    @Override
    public boolean deposit(long accountNo, double amount) {
        if (amount <= 0) return false;

        Account acc = accountDAO.getAccountByNo(accountNo);
        if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE) return false;

        double newBalance = acc.getBalance() + amount;
        return accountDAO.updateBalance(accountNo, newBalance);
    }

    @Override
    public boolean withdraw(long accountNo, double amount) {
        if (amount <= 0) return false;

        Account acc = accountDAO.getAccountByNo(accountNo);
        if (acc == null || acc.getAccount_status() != AccountStatus.ACTIVE) return false;
        if (acc.getBalance() < amount) return false;

        double newBalance = acc.getBalance() - amount;
        return accountDAO.updateBalance(accountNo, newBalance);
    }

    @Override
    public double checkBalance(long accountNo) {
        Account acc = accountDAO.getAccountByNo(accountNo);
        return (acc != null) ? acc.getBalance() : 0.0;
    }

    @Override
    public boolean transfer(long fromAccountNo, long toAccountNo, double amount) {
        if (amount <= 0) return false;

        Account fromAcc = accountDAO.getAccountByNo(fromAccountNo);
        Account toAcc = accountDAO.getAccountByNo(toAccountNo);

        if (fromAcc == null || toAcc == null) return false;
        if (fromAcc.getBalance() < amount) return false;

        double newFromBal = fromAcc.getBalance() - amount;
        double newToBal = toAcc.getBalance() + amount;

        return accountDAO.updateBalance(fromAccountNo, newFromBal)
                && accountDAO.updateBalance(toAccountNo, newToBal);
    }
    
    public long generateAccountNo() {
		long highest_accountNo = new AccountDaoImp().getHighestAccountNo();
		return highest_accountNo+1;
    }

}
