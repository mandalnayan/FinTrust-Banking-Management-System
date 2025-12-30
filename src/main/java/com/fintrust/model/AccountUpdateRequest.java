package com.fintrust.model;
import java.time.LocalDateTime;

public class AccountUpdateRequest {
    private long requestId;
    private long accountNo;
    private String newAccountType;
    private String newBranchName;
    private long branchId;
    private String newModeOfOperation;
    private String status;
    
    //User details
    private User requestedBy;
    
    //Admin details
    private User reviewBy;
    
    private LocalDateTime requestDate;
    private LocalDateTime reviewDate;
    
    
   // Getters & Setters
	public long getRequestId() {
		return requestId;
	}
	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}
	public long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(long accountNo) {
		this.accountNo = accountNo;
	}
	public String getNewAccountType() {
		return newAccountType;
	}
	public void setNewAccountType(String newAccountType) {
		this.newAccountType = newAccountType;
	}
	public String getNewBranchName() {
		return newBranchName;
	}
	public void setNewBranchName(String newBranchName) {
		this.newBranchName = newBranchName;
	}
	public long getBranchId() {
		return branchId;
	}
	public void setBranchId(long branchId) {
		this.branchId = branchId;
	}
	public String getNewModeOfOperation() {
		return newModeOfOperation;
	}
	public void setNewModeOfOperation(String newModeOfOperation) {
		this.newModeOfOperation = newModeOfOperation;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public User getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}
	public User getReviewBy() {
		return reviewBy;
	}
	public void setReviewBy(User reviewBy) {
		this.reviewBy = reviewBy;
	}
	public LocalDateTime getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}
	public LocalDateTime getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(LocalDateTime reviewDate) {
		this.reviewDate = reviewDate;
	}
	@Override
	public String toString() {
		return "AccountUpdateRequest [requestId=" + requestId + ", accountNo=" + accountNo + ", newAccountType="
				+ newAccountType + ", newBranchName=" + newBranchName + ", branchId=" + branchId
				+ ", newModeOfOperation=" + newModeOfOperation + ", status=" + status + ", requestedBy=" + requestedBy
				+ ", reviewBy=" + reviewBy + ", requestDate=" + requestDate + ", reviewDate=" + reviewDate + "]";
	}
}
