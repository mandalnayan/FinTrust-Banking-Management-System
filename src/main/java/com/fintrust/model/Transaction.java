package com.fintrust.model;

public class Transaction {
private long id;
private long from_account;
private long to_account;
private double amount;
private String status;
private String created_at;




public Transaction(long id, long from_account, long to_account, double amount, String status, String created_at) {
	super();
	this.id = id;
	this.from_account = from_account;
	this.to_account = to_account;
	this.amount = amount;
	this.status = status;
	this.created_at = created_at;
}
public long getId() {
	return id;
}
public void setId(long id) {
	this.id = id;
}
public long getFrom_account() {
	return from_account;
}
public void setFrom_account(long from_account) {
	this.from_account = from_account;
}
public long getTo_account() {
	return to_account;
}
public void setTo_account(long to_account) {
	this.to_account = to_account;
}
public double getAmount() {
	return amount;
}
public void setAmount(double amount) {
	this.amount = amount;
}
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getCreated_at() {
	return created_at;
}
public void setCreated_at(String created_at) {
	this.created_at = created_at;
}
@Override
public String toString() {
	return "Transaction [id=" + id + ", from_account=" + from_account + ", to_account=" + to_account + ", amount="
			+ amount + ", status=" + status + ", created_at=" + created_at + "]";
}


}

