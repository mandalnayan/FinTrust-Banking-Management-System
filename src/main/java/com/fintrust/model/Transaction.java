//package com.fintrust.model;
//
//import java.math.BigDecimal;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//public class Transaction {
//	private Long transactionId;
//	private Long accountNumber;
//	private Long counterparty_account_number;
//	private Long beneficiaryId;
//	private String txnReference;
//	private String txnType; // credit/debit
//	private String mode; // upi/neft/imps/card/cash
//	private Double amount;
//	private BigDecimal balanceAfter;
//	private String description;
//	private TransactionStatus status;
//	private LocalDateTime createdAt;
//
//	public Transaction() {
//
//	}
////	'status', 'enum(\'pending\',\'completed\',\'failed\',\'reversed\')', 'NO', '', 'pending', ''
//
//	public enum TransactionStatus {
//		PENDING, FAILED, REVERSED, COMPLETED
//	}
//
//	public Transaction(Long transactionId, Long accountNumber, Long relatedAccountNumber, Long beneficiaryId,
//			String txnReference, String txnType, String mode, Double amount, BigDecimal balanceAfter,
//			String description, String status, Timestamp createdAt) {
//		super();
//		this.transactionId = transactionId;
//		this.accountNumber = accountNumber;
//		this.counterparty_account_number = relatedAccountNumber;
//		this.beneficiaryId = beneficiaryId;
//		this.txnReference = txnReference;
//		this.txnType = txnType;
//		this.mode = mode;
//		this.amount = amount;
//		this.balanceAfter = balanceAfter;
//		this.description = description;
//		this.status = TransactionStatus.valueOf(status.toUpperCase());
//		this.createdAt = createdAt.toLocalDateTime();
//	}
//
//	public Long getTransactionId() {
//		return transactionId;
//	}
//
//	public void setTransactionId(Long transactionId) {
//		this.transactionId = transactionId;
//	}	
//
//	public Long getAccountNumber() {
//		return accountNumber;
//	}
//
//	public void setAccountNumber(Long accountNumber) {
//		this.accountNumber = accountNumber;
//	}
//
//	public Long getCounterparty_account_number() {
//		return counterparty_account_number;
//	}
//
//	public void setCounterparty_account_number(Long counterparty_account_number) {
//		this.counterparty_account_number = counterparty_account_number;
//	}
//
//	public void setStatus(TransactionStatus status) {
//		this.status = status;
//	}
//
//	public Long getBeneficiaryId() {
//		return beneficiaryId;
//	}
//
//	public void setBeneficiaryId(Long beneficiaryId) {
//		this.beneficiaryId = beneficiaryId;
//	}
//
//	public String getTxnReference() {
//		return txnReference;
//	}
//
//	public void setTxnReference(String txnReference) {
//		this.txnReference = txnReference;
//	}
//
//	public String getTxnType() {
//		return txnType;
//	}
//
//	public void setTxnType(String txnType) {
//		this.txnType = txnType;
//	}
//
//	public String getMode() {
//		return mode;
//	}
//
//	public void setMode(String mode) {
//		this.mode = mode;
//	}
//
//	public Double getAmount() {
//		return amount;
//	}
//
//	public void setAmount(Double amount) {
//		this.amount = amount;
//	}
//
//	public BigDecimal getBalanceAfter() {
//		return balanceAfter;
//	}
//
//	public void setBalanceAfter(BigDecimal balanceAfter) {
//		this.balanceAfter = balanceAfter;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
//
//	public String getStatus() {
//		return status.name();
//	}
//
//	public void setStatus(String status) {
//		this.status = TransactionStatus.valueOf(status.toUpperCase());
//	}
//
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(Timestamp createdAt) {
//		this.createdAt = createdAt.toLocalDateTime();
//	}
//
//}


//
//public class Transaction {
//private long id;
//private long from_account;
//private long to_account;
//private double amount;
//private String status;
//private String created_at;
//
//
//
//
//public Transaction(long id, long from_account, long to_account, double amount, String status, String created_at) {
//	super();
//	this.id = id;
//	this.from_account = from_account;
//	this.to_account = to_account;
//	this.amount = amount;
//	this.status = status;
//	this.created_at = created_at;
//}
//public long getId() {
//	return id;
//}
//public void setId(long id) {
//	this.id = id;
//}
//public long getFrom_account() {
//	return from_account;
//}
//public void setFrom_account(long from_account) {
//	this.from_account = from_account;
//}
//public long getTo_account() {
//	return to_account;
//}
//public void setTo_account(long to_account) {
//	this.to_account = to_account;
//}
//public double getAmount() {
//	return amount;
//}
//public void setAmount(double amount) {
//	this.amount = amount;
//}
//public String getStatus() {
//	return status;
//}
//public void setStatus(String status) {
//	this.status = status;
//}
//public String getCreated_at() {
//	return created_at;
//}
//public void setCreated_at(String created_at) {
//	this.created_at = created_at;
//}
//@Override
//public String toString() {
//	return "Transaction [id=" + id + ", from_account=" + from_account + ", to_account=" + to_account + ", amount="
//			+ amount + ", status=" + status + ", created_at=" + created_at + "]";
//}
//
//
//}
//











package com.fintrust.model;


//
//public class Transaction {
//private long id;
//private long from_account;
//private long to_account;
//private double amount;
//private String status;
//private String created_at;
//
//
//
//
//public Transaction(long id, long from_account, long to_account, double amount, String status, String created_at) {
//	super();
//	this.id = id;
//	this.from_account = from_account;
//	this.to_account = to_account;
//	this.amount = amount;
//	this.status = status;
//	this.created_at = created_at;
//}
//public long getId() {
//	return id;
//}
//public void setId(long id) {
//	this.id = id;
//}
//public long getFrom_account() {
//	return from_account;
//}
//public void setFrom_account(long from_account) {
//	this.from_account = from_account;
//}
//public long getTo_account() {
//	return to_account;
//}
//public void setTo_account(long to_account) {
//	this.to_account = to_account;
//}
//public double getAmount() {
//	return amount;
//}
//public void setAmount(double amount) {
//	this.amount = amount;
//}
//public String getStatus() {
//	return status;
//}
//public void setStatus(String status) {
//	this.status = status;
//}
//public String getCreated_at() {
//	return created_at;
//}
//public void setCreated_at(String created_at) {
//	this.created_at = created_at;
//}
//@Override
//public String toString() {
//	return "Transaction [id=" + id + ", from_account=" + from_account + ", to_account=" + to_account + ", amount="
//			+ amount + ", status=" + status + ", created_at=" + created_at + "]";
//}
//
//
//}
//
















//
//public class Transaction {
//private long id;
//private long from_account;
//private long to_account;
//private double amount;
//private String status;
//private String created_at;
//
//
//
//
//public Transaction(long id, long from_account, long to_account, double amount, String status, String created_at) {
//	super();
//	this.id = id;
//	this.from_account = from_account;
//	this.to_account = to_account;
//	this.amount = amount;
//	this.status = status;
//	this.created_at = created_at;
//}
//public long getId() {
//	return id;
//}
//public void setId(long id) {
//	this.id = id;
//}
//public long getFrom_account() {
//	return from_account;
//}
//public void setFrom_account(long from_account) {
//	this.from_account = from_account;
//}
//public long getTo_account() {
//	return to_account;
//}
//public void setTo_account(long to_account) {
//	this.to_account = to_account;
//}
//public double getAmount() {
//	return amount;
//}
//public void setAmount(double amount) {
//	this.amount = amount;
//}
//public String getStatus() {
//	return status;
//}
//public void setStatus(String status) {
//	this.status = status;
//}
//public String getCreated_at() {
//	return created_at;
//}
//public void setCreated_at(String created_at) {
//	this.created_at = created_at;
//}
//@Override
//public String toString() {
//	return "Transaction [id=" + id + ", from_account=" + from_account + ", to_account=" + to_account + ", amount="
//			+ amount + ", status=" + status + ", created_at=" + created_at + "]";
//}
//
//
//}
//


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

