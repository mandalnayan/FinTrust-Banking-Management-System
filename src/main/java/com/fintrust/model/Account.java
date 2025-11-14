package com.fintrust.model;
import java.time.LocalDateTime;

public class Account {
	private long account_no;
	private long customer_id;
	private double balance;
	private AccountType account_type;
	private AccountStatus account_status;
	private String branch_Name;
	private ModeOfOperation mode_of_operation;
	private long nominee_id ;
	private LocalDateTime created_at;
	
	
	
	public Account(long account_no, long customer_id, double balance, AccountType account_type,
			AccountStatus account_status, String branch_Name, ModeOfOperation mode_of_operation, long nominee_id,
			LocalDateTime created_at) {
		super();
		this.account_no = account_no;
		this.customer_id = customer_id;
		this.balance = balance;
		this.account_type = account_type;
		this.account_status = account_status;
		this.branch_Name = branch_Name;
		this.mode_of_operation = mode_of_operation;
		this.nominee_id = nominee_id;
		this.created_at = created_at;
	}

	public Account() {
		// TODO Auto-generated constructor stub
	}

	public enum AccountStatus {
		ACTIVE,INACTIVE,CLOSED
	}
	
	public enum AccountType {
		CURRENT,SAVING,SALARY
	}
	
	public enum ModeOfOperation{
		SELF, JOINT 
	}
	
	public long getAccount_no() {
		return account_no;
	}

	public void setAccount_no(long account_no) {
		this.account_no = account_no;
	}

	public long getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(long customer_id) {
		this.customer_id = customer_id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public AccountType getAccount_type() {
		return account_type;
	}

	public void setAccount_type(AccountType account_type) {
		this.account_type = account_type;
	}

	public AccountStatus getAccount_status() {
		return account_status;
	}

	public void setAccount_status(AccountStatus account_status) {
		this.account_status = account_status;
	}

	public String getBranch_Name() {
		return branch_Name;
	}

	public void setBranch_Name(String branch_Name) {
		this.branch_Name = branch_Name;
	}

	public ModeOfOperation getMode_of_operation() {
		return mode_of_operation;
	}

	public void setMode_of_operation(ModeOfOperation mode_of_operation) {
		this.mode_of_operation = mode_of_operation;
	}

	public long getNominee_id() {
		return nominee_id;
	}

	public void setNominee_id(long nominee_id) {
		this.nominee_id = nominee_id;
	}

	public LocalDateTime getCreated_at() {
		return created_at;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	@Override
	public String toString() {
		return "Account [account_no=" + account_no + ", customer_id=" + customer_id + ", balance=" + balance
				+ ", account_type=" + account_type + ", account_status=" + account_status + ", branch_Name="
				+ branch_Name + ", mode_of_operation=" + mode_of_operation + ", nominee_id=" + nominee_id
				+ ", created_at=" + created_at + "]";
	}
	
	
}
