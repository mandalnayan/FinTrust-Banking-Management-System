package com.fintrust.model;

public class Beneficiary {

    private Long beneficiaryId;
    private Long userId;
    private Long accountNumber;
    private String name;
    private String bankName;
    private String ifscCode;
    private java.sql.Timestamp addedAt;

    // --- Constructors ---
    public Beneficiary() {}

    public Beneficiary(Long beneficiaryId, Long userId, Long accountNumber, String name,
                       String bankName, String ifscCode, java.sql.Timestamp addedAt) {
        this.beneficiaryId = beneficiaryId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.name = name;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.addedAt = addedAt;
    }

    // --- Getters & Setters ---
    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public java.sql.Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(java.sql.Timestamp addedAt) {
        this.addedAt = addedAt;
    }
}

