package com.fintrust.model;

import java.sql.Timestamp;

public class CardRequest {
	    private long requestNo;
	    private long accountNo;
	    private String cardType;
	    private String cardCategory;
	    private String address;
	    private String remarks;
	    private String status;
	    private Timestamp requestedAt;
	    
		public long getRequestNo() {
			return requestNo;
		}
		public void setRequestNo(long requestNo) {
			this.requestNo = requestNo;
		}
		public long getAccountNo() {
			return accountNo;
		}
		public void setAccountNo(long accountNo) {
			this.accountNo = accountNo;
		}
		public String getCardType() {
			return cardType;
		}
		public void setCardType(String cardType) {
			this.cardType = cardType;
		}
		public String getCardCategory() {
			return cardCategory;
		}
		public void setCardCategory(String cardCategory) {
			this.cardCategory = cardCategory;
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
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}

		public String getRequestedAt() {
			return status;
		}
		public void setRequestedAt(Timestamp requestAt) {
			this.requestedAt = requestAt;
		}
	   
	    
	}


