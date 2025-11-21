package com.fintrust.model;

import java.time.LocalDateTime;

public class Bank {

	private Long bankId;
	private String bankName;
	private String bankCode;
	private String ifscPrefix;
	private String branchName;
	private String supportEmail;
	private String supportPhone;
	private LocalDateTime createdAt;
	
	public Bank() {}
	
	public Bank(Long bankId, String bankName, String bankCode, String ifscPrefix, String branchName,
			String supportEmail, String supportPhone, LocalDateTime createdAt) {
		super();
		this.bankId = bankId;
		this.bankName = bankName;
		this.bankCode = bankCode;
		this.ifscPrefix = ifscPrefix;
		this.branchName = branchName;
		this.supportEmail = supportEmail;
		this.supportPhone = supportPhone;
		this.createdAt = createdAt;
	}
	public Long getBankId() {
		return bankId;
	}
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getIfscPrefix() {
		return ifscPrefix;
	}
	public void setIfscPrefix(String ifscPrefix) {
		this.ifscPrefix = ifscPrefix;
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
