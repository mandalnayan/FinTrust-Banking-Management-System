package com.fintrust.model;
import java.time.LocalDateTime;

public class AccountUpdateRequest {
    private long requestId;
    private long accountNo;
    private String newAccountType;
    private String newBranchName;
    private String newModeOfOperation;
    private String status;
    private long requestedBy;
    private Long reviewedBy;
    private LocalDateTime requestDate;
    private LocalDateTime reviewDate;

    // Getters & Setters
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }

    public long getAccountNo() { return accountNo; }
    public void setAccountNo(long accountNo) { this.accountNo = accountNo; }

    public String getNewAccountType() { return newAccountType; }
    public void setNewAccountType(String newAccountType) { this.newAccountType = newAccountType; }

    public String getNewBranchName() { return newBranchName; }
    public void setNewBranchName(String newBranchName) { this.newBranchName = newBranchName; }

    public String getNewModeOfOperation() { return newModeOfOperation; }
    public void setNewModeOfOperation(String newModeOfOperation) { this.newModeOfOperation = newModeOfOperation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getRequestedBy() { return requestedBy; }
    public void setRequestedBy(long requestedBy) { this.requestedBy = requestedBy; }

    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
    
    
	@Override
	public String toString() {
		return "AccountUpdateRequest [requestId=" + requestId + ", accountNo=" + accountNo + ", newAccountType="
				+ newAccountType + ", newBranchName=" + newBranchName + ", newModeOfOperation=" + newModeOfOperation
				+ ", status=" + status + ", requestedBy=" + requestedBy + ", reviewedBy=" + reviewedBy
				+ ", requestDate=" + requestDate + ", reviewDate=" + reviewDate + "]";
	}
    
    
    
}
