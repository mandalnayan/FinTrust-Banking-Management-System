package com.fintrust.model;


import java.time.LocalDateTime;
import java.util.List;



public class ContactModel {
    private Long id;
    private String customerName;
    private String mobile;
    private String email;
    private String customerId;
    private String category;
    private String subcategory;
    private String priority;
    private String subject;
    private String description;
    private String preferredContactMethod;
    private String preferredTime;
    private String status;
    
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubcategory() {
		return subcategory;
	}
	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPreferredContactMethod() {
		return preferredContactMethod;
	}
	public void setPreferredContactMethod(String preferredContactMethod) {
		this.preferredContactMethod = preferredContactMethod;
	}
	public String getPreferredTime() {
		return preferredTime;
	}
	public void setPreferredTime(String preferredTime) {
		this.preferredTime = preferredTime;
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
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public List<String> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> attachments;


	@Override
	public String toString() {
		return "ContactModel [id=" + id + ", customerName=" + customerName + ", mobile=" + mobile + ", email=" + email
				+ ", customerId=" + customerId + ", category=" + category + ", subcategory=" + subcategory
				+ ", priority=" + priority + ", subject=" + subject + ", description=" + description
				+ ", preferredContactMethod=" + preferredContactMethod + ", preferredTime=" + preferredTime
				+ ", status=" + status + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", attachments="
				+ attachments + "]";
	} 
    
    
}



