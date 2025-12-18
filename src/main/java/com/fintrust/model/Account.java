package com.fintrust.model;
import java.time.LocalDateTime;

public class Account {
    private Long accountId;
    private Long userId;
    private Long branchId; 
    private Long nominee_id ;
    private Long accountNumber;
    private AccountType accountType;   // savings/current/salary/fixed_deposit
    private Double balance;
    private String currency;
    private AccountStatus account_status;
    private AccountOwnershipType accountOwnershipType;
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
	
	// Account Ownership Type
	public enum AccountOwnershipType{
		SELF,JOINT
	}
    
    public Account() {
    	
    }

	public Account(Long accountId, Long userId, Long branchId, Long nominee_id, Long accountNumber,
			AccountType accountType, Double balance, String currency, AccountStatus account_status,
			AccountOwnershipType accountOwnershipType, LocalDateTime openedAt, LocalDateTime updatedAt) {
		super();
		this.accountId = accountId;
		this.userId = userId;
		this.branchId = branchId;
		this.nominee_id = nominee_id;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
		this.balance = balance;
		this.currency = currency;
		this.account_status = account_status;
		this.accountOwnershipType = accountOwnershipType;
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

	public Long getBranchId() {
		return branchId;
	}

	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}

	public Long getNominee_id() {
		return nominee_id;
	}

	public void setNominee_id(Long nominee_id) {
		this.nominee_id = nominee_id;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
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

	public AccountStatus getAccount_status() {
		return account_status;
	}

	public void setAccount_status(AccountStatus account_status) {
		this.account_status = account_status;
	}

	public AccountOwnershipType getAccountOwnershipType() {
		return accountOwnershipType;
	}

	public void setAccountOwnershipType(AccountOwnershipType accountOwnershipType) {
		this.accountOwnershipType = accountOwnershipType;
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
