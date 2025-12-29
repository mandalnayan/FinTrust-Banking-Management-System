package com.fintrust.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class CardRequest {
    private long request_no;
    private String card_type;
    private String card_category;
    private String address;
    private String remarks;
    private String card_request_status;
    private long user_id;
    private long account_no;
    private LocalDateTime requested_at;
    private LocalDateTime approved_at;
	public long getRequest_no() {
		return request_no;
	}
	public void setRequest_no(long request_no) {
		this.request_no = request_no;
	}
	public String getCard_type() {
		return card_type;
	}
	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}
	public String getCard_category() {
		return card_category;
	}
	public void setCard_category(String card_category) {
		this.card_category = card_category;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getCard_request_status() {
		return card_request_status;
	}
	public void setCard_request_status(String card_request_status) {
		this.card_request_status = card_request_status;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public long getAccount_no() {
		return account_no;
	}
	public void setAccount_no(long account_no) {
		this.account_no = account_no;
	}
	public LocalDateTime getRequested_at() {
		return requested_at;
	}
	public void setRequested_at(LocalDateTime date) {
		this.requested_at = date;
	}
	public LocalDateTime getApproved_at() {
		return approved_at;
	}
	public void setApproved_at(LocalDateTime approved_at) {
		this.approved_at = approved_at;
	}
	@Override
	public String toString() {
		return "CardRequest [request_no=" + request_no + ", card_type=" + card_type + ", card_category=" + card_category
				+ ", address=" + address + ", remarks=" + remarks + ", card_request_status=" + card_request_status
				+ ", user_id=" + user_id + ", account_no=" + account_no + ", requested_at=" + requested_at
				+ ", approved_at=" + approved_at + "]";
	}
	public CardRequest(long request_no, String card_type, String card_category, String address, String remarks,
			String card_request_status, long user_id, long account_no, LocalDateTime requested_at,
			LocalDateTime approved_at) {
		super();
		this.request_no = request_no;
		this.card_type = card_type;
		this.card_category = card_category;
		this.address = address;
		this.remarks = remarks;
		this.card_request_status = card_request_status;
		this.user_id = user_id;
		this.account_no = account_no;
		this.requested_at = requested_at;
		this.approved_at = approved_at;
	}
    
	public CardRequest() {}
    
}
