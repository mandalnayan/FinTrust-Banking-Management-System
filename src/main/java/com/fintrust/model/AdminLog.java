package com.fintrust.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AdminLog {
    private Long logId;
    private Long adminId;
    private String action;
    private String metadataJson;
    private LocalDateTime createdAt;

    public AdminLog() {
    	
    }

	public AdminLog(Long logId, Long adminId, String action, String metadataJson, LocalDateTime createdAt) {
		super();
		this.logId = logId;
		this.adminId = adminId;
		this.action = action;
		this.metadataJson = metadataJson;
		this.createdAt = createdAt;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMetadataJson() {
		return metadataJson;
	}

	public void setMetadataJson(String metadataJson) {
		this.metadataJson = metadataJson;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}
