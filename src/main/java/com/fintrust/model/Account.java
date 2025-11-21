package com.fintrust.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String accountType;   // savings/current/salary/fixed_deposit
    private BigDecimal balance;
    private String currency;
    private String status;
    private LocalDateTime openedAt;
    private LocalDateTime updatedAt;
    
    public Account() {
    	
    }
    
	public Account(Long accountId, Long userId, String accountNumber, String accountType, BigDecimal balance,
			String currency, String status, LocalDateTime openedAt, LocalDateTime updatedAt) {
		super();
		this.accountId = accountId;
		this.userId = userId;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.balance = balance;
		this.currency = currency;
		this.status = status;
		this.openedAt = openedAt;
		this.updatedAt = updatedAt;
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
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getOpenedAt() {
		return openedAt;
	}
	public void setOpenedAt(LocalDateTime openedAt) {
		this.openedAt = openedAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

   
    
    
}
