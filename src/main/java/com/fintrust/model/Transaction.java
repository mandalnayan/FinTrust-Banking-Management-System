package com.fintrust.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Transaction {
	private Long transactionId;
	private Long accountNumber;
	private Long counterparty_account_number;
	private Long beneficiaryId;
	private String txnReference;
	private String txnType; // credit/debit
	private String mode; // upi/neft/imps/card/cash
	private Double amount;
	private BigDecimal balanceAfter;
	private String description;
	private TransactionStatus status;
	private LocalDateTime createdAt;

	public Transaction() {

	}
//	'status', 'enum(\'pending\',\'completed\',\'failed\',\'reversed\')', 'NO', '', 'pending', ''

	public enum TransactionStatus {
		PENDING, FAILED, REVERSED, COMPLETED
	}

	public Transaction(Long transactionId, Long accountNumber, Long relatedAccountNumber, Long beneficiaryId,
			String txnReference, String txnType, String mode, Double amount, BigDecimal balanceAfter,
			String description, String status, Timestamp createdAt) {
		super();
		this.transactionId = transactionId;
		this.accountNumber = accountNumber;
		this.counterparty_account_number = relatedAccountNumber;
		this.beneficiaryId = beneficiaryId;
		this.txnReference = txnReference;
		this.txnType = txnType;
		this.mode = mode;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.status = TransactionStatus.valueOf(status.toUpperCase());
		this.createdAt = createdAt.toLocalDateTime();
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}	

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Long getCounterparty_account_number() {
		return counterparty_account_number;
	}

	public void setCounterparty_account_number(Long counterparty_account_number) {
		this.counterparty_account_number = counterparty_account_number;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Long getBeneficiaryId() {
		return beneficiaryId;
	}

	public void setBeneficiaryId(Long beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	public String getTxnReference() {
		return txnReference;
	}

	public void setTxnReference(String txnReference) {
		this.txnReference = txnReference;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status.name();
	}

	public void setStatus(String status) {
		this.status = TransactionStatus.valueOf(status.toUpperCase());
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt.toLocalDateTime();
	}

}
