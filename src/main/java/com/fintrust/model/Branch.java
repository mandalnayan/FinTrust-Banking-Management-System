package com.fintrust.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Branch {

	private Long branchId;
	private String ifscCode;
	private String branchName;
	private String supportEmail;
	private String supportPhone;
	private LocalDateTime createdAt;
	
	public Branch() {}

	public Branch(Long branchId, String ifscCode, String branchName, String supportEmail, String supportPhone,
			LocalDateTime createdAt) {
		super();
		this.branchId = branchId;
		this.ifscCode = ifscCode;
		this.branchName = branchName;
		this.supportEmail = supportEmail;
		this.supportPhone = supportPhone;
		this.createdAt = createdAt;
	}


	public Long getBranchId() {
		return branchId;
	}


	public void setBranchId(Long branchId) {
		this.branchId = branchId;
	}


	public String getIfscCode() {
		return ifscCode;
	}


	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public String getSupportEmail() {
		return supportEmail;
	}


	public void setSupportEmail(String supportEmail) {
		this.supportEmail = supportEmail;
	}


	public String getSupportPhone() {
		return supportPhone;
	}


	public void setSupportPhone(String supportPhone) {
		this.supportPhone = supportPhone;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
