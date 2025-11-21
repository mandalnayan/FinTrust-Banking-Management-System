package com.fintrust.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan {
    private Long loanId;
    private Long userId;
    private String loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
	
    public Loan() {
    	
    }
    
    
    public Loan(Long loanId, Long userId, String loanType, BigDecimal principalAmount, BigDecimal interestRate,
			Integer tenureMonths, String status, LocalDateTime createdAt, LocalDateTime approvedAt) {
		super();
		this.loanId = loanId;
		this.userId = userId;
		this.loanType = loanType;
		this.principalAmount = principalAmount;
		this.interestRate = interestRate;
		this.tenureMonths = tenureMonths;
		this.status = status;
		this.createdAt = createdAt;
		this.approvedAt = approvedAt;
	}
    
    
	public Long getLoanId() {
		return loanId;
	}
	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public BigDecimal getPrincipalAmount() {
		return principalAmount;
	}
	public void setPrincipalAmount(BigDecimal principalAmount) {
		this.principalAmount = principalAmount;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
	public Integer getTenureMonths() {
		return tenureMonths;
	}
	public void setTenureMonths(Integer tenureMonths) {
		this.tenureMonths = tenureMonths;
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
	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}
	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

    
}