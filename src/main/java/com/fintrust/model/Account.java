package com.fintrust.model;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fintrust.model_copy.Account.AccountStatus;

public class Account {
    private Long accountId;
    private Long userId;
    private Long bankId;
    private long nominee_id ;
    private String accountNumber;
    private AccountType accountType;   // savings/current/salary/fixed_deposit
    private Double balance;
    private String currency;
    private AccountStatus account_status;
    private LocalDateTime openedAt;
    private LocalDateTime updatedAt;
    
    //** Account status
	public enum AccountStatus {
		ACTIVE,INACTIVE,CLOSED
	}
	
	// Account Type
	public enum AccountType {
		SAVINGS, CURRENT, SALARY
	}
    
    public Account() {
    	
    }
    
	public Account(Long accountId, Long userId, Long bankId, String accountNumber, String accountType, BigDecimal balance,
			String currency, String status, Timestamp openedAt, Timestamp updatedAt) {
		super();
		this.accountId = accountId;
		this.userId = userId;
		this.bankId = bankId;
		this.accountNumber = accountNumber;
		this.accountType = AccountType.valueOf(accountType.toUpperCase());
		this.balance = balance.doubleValue();
		this.currency = currency;
		this.account_status = AccountStatus.valueOf(status.toUpperCase());		
		this.openedAt = openedAt.toLocalDateTime();
		this.updatedAt = updatedAt.toLocalDateTime();
	}
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long bankId) {
		this.bankId = bankId;
	}
	public Long getBankId() {
		return userId;
	}
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	public long getNominee_id() {
		return nominee_id;
	}

	public void setNominee_id(long nominee_id) {
		this.nominee_id = nominee_id;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountType() {
		return accountType.name();
	}
	public void setAccountType(String accountType) {
		this.accountType = AccountType.valueOf(accountType.toUpperCase());
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public AccountStatus getStatus() {
		return account_status;
	}
	public void setStatus(AccountStatus status) {
		this.account_status = status;
	}

	public LocalDateTime getOpenedAt() {
		return openedAt;
	}
	public void setOpenedAt(Timestamp openedAt) {
		this.openedAt = openedAt.toLocalDateTime();
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

   
    
    
}
