package com.fintrust.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanPayment {
    private Long paymentId;
    private Long loanId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String method;
    private String status;
    
    public LoanPayment() {
    	
    }
    
    
	public LoanPayment(Long paymentId, Long loanId, BigDecimal amount, LocalDateTime paymentDate, String method,
			String status) {
		super();
		this.paymentId = paymentId;
		this.loanId = loanId;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.method = method;
		this.status = status;
	}
	
	
	public Long getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}
	public Long getLoanId() {
		return loanId;
	}
	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

    
}
