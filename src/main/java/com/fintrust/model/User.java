package com.fintrust.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Represents a user record in the banking system.
 * <p>
 * This model is immutable to ensure security, stability, and prevention
 * of accidental modifications, which is a key requirement in financial applications.
 */


public class User {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_SUPER_ADMIN
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        BLOCKED
    }

    public User() {
    }

	public User(Long id, String name, String email, String phone, String role, String status,
			String password_hash,Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.fullName = name;
        this.email = email;
        this.phone = phone;
        this.role = Role.valueOf(role.toUpperCase());
        this.status = Status.valueOf(status.toUpperCase());
        this.password = password_hash;
        this.createdAt = createdAt.toLocalDateTime();
        this.updatedAt = updatedAt.toLocalDateTime();
    }


    public Long getId() {
        return id;
    }


    public String getFullName() {
		return fullName;
	}

	public void setFullName(String name) {
		this.fullName = name;
	}
    
    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", Full Name ='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", password=" + password +
                '}';
    }
}

