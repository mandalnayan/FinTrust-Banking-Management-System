package com.fintrust.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private Long transactionId;
    private Long userId;
    private Long accountNumber;
    private Long counterpartyAccountNumber;
    private Long beneficiaryId;
    private String txnReference;
    private String txnType;
    private String mode;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    // Enums matching DB enums
//    public enum TxnType {
//        CREDIT,
//        DEBIT
//    }
//
//    public enum Mode {
//        ONLINE,
//        UPI,
//        CARD,
//        NEFT,
//        RTGS,
//        IMPS,
//        CASH
//    }
//
//    public enum Status {
//        PENDING,
//        COMPLETED,
//        FAILED,
//        REVERSED
//    }


    // Constructors
    public Transaction() {
    }

    public Transaction(Long transactionId, Long userId, Long accountNumber, 
                       Long counterpartyAccountNumber, Long beneficiaryId,
                       String txnReference, String txnType, String mode,
                       BigDecimal amount, BigDecimal balanceAfter,
                       String description, String status, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.counterpartyAccountNumber = counterpartyAccountNumber;
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

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public Long getCounterpartyAccountNumber() {
        return counterpartyAccountNumber;
    }

    public void setCounterpartyAccountNumber(Long counterpartyAccountNumber) {
        this.counterpartyAccountNumber = counterpartyAccountNumber;
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

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", userId=" + userId + ", accountNumber=" + accountNumber
				+ ", counterpartyAccountNumber=" + counterpartyAccountNumber + ", beneficiaryId=" + beneficiaryId
				+ ", txnReference=" + txnReference + ", txnType=" + txnType + ", mode=" + mode + ", amount=" + amount
				+ ", balanceAfter=" + balanceAfter + ", description=" + description + ", status=" + status
				+ ", createdAt=" + createdAt + "]";
	}
	
	
	
}
