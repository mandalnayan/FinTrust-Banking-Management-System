package com.fintrust.model;

public class Notification {

	private String type;
	private String message;
	
	public Notification(String type, String mes) {
		this.type = type;
		this.message = mes;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	
	
}
