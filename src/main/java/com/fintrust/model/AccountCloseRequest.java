package com.fintrust.model;

import java.time.LocalDateTime;

public class AccountCloseRequest {
	private long requestId;
	private long accountNo;
	private String reason;
	private String status;
	private long requestedBy;
    private Long reviewedBy;
    private LocalDateTime requestDate;
    private LocalDateTime reviewDate;
	private String remarks;
	
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
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getRequestedBy() {
		return requestedBy;
	}
	public void setRequestedBy(long requestedBy) {
		this.requestedBy = requestedBy;
	}
	public Long getReviewedBy() {
		return reviewedBy;
	}
	public void setReviewedBy(Long reviewedBy) {
		this.reviewedBy = reviewedBy;
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
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Override
	public String toString() {
		return "AccountCloseRequest [requestId=" + requestId + ", accountNo=" + accountNo + ", reason=" + reason
				+ ", status=" + status + ", requestedBy=" + requestedBy + ", reviewedBy=" + reviewedBy
				+ ", requestDate=" + requestDate + ", reviewDate=" + reviewDate + ", remarks=" + remarks + "]";
	}
}
