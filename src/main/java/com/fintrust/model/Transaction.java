package com.fintrust.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long transactionId;
    private Long accountId;
    private Long relatedAccountId;
    private Long beneficiaryId;
    private String txnReference;
    private String txnType;       // credit/debit
    private String mode;          // upi/neft/imps/card/cash
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    
    
    public Transaction() {
    	
    }
    
    
    
	public Transaction(Long transactionId, Long accountId, Long relatedAccountId, Long beneficiaryId,
			String txnReference, String txnType, String mode, BigDecimal amount, BigDecimal balanceAfter,
			String description, String status, LocalDateTime createdAt) {
		super();
		this.transactionId = transactionId;
		this.accountId = accountId;
		this.relatedAccountId = relatedAccountId;
		this.beneficiaryId = beneficiaryId;
		this.txnReference = txnReference;
		this.txnType = txnType;
		this.mode = mode;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.status = status;
		this.createdAt = createdAt;
	}
	
	
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Long getRelatedAccountId() {
		return relatedAccountId;
	}
	public void setRelatedAccountId(Long relatedAccountId) {
		this.relatedAccountId = relatedAccountId;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
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
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

   
    
}


