package com.fintrust.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.fintrust.model.User.KycStatus;

/**
 * Represents a user complete record in the banking system.
 * <p>
 * */

public class UserSessionDetails implements Serializable{

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private User.KycStatus status;
    private long primaryAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;  
            
	public UserSessionDetails(Long id, String fullName, String email, String phone, String address,
			KycStatus status, long primaryAccountId, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.status = status;
		this.primaryAccountId = primaryAccountId;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User.KycStatus getStatus() {
		return status;
	}
	public void setStatus(User.KycStatus status) {
		this.status = status;
	}
	public long getPrimaryAccountId() {
		return primaryAccountId;
	}
	public void setPrimaryAccountId(long primaryAccountId) {
		this.primaryAccountId = primaryAccountId;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}

