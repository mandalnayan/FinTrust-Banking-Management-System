package com.fintrust.dao;

import java.util.List;

import com.fintrust.model.Account;

public interface AccountDao {
	boolean createAccount(Account account);

	Account getAccountByNo(long accountNo);
	List<Account> getAllAccounts();
	List<Account> getAllAccounts(long customerId);

	boolean updateAccount(Account account);
	boolean updateBalance(long accountNo, double newBalance);
	boolean updateAccountStatus(long accountNo, String status);

	boolean deleteAccount(long accountNo);

	boolean isSameAccount(long customer_id, String accountType);

	Account getDefaultAccount();
	
	List<Long> getAccountsByUserId(Long userid);
		
	
}
