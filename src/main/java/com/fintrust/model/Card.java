package com.fintrust.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Card {
	private Long cardId;
    private Long userId;
    private Long accountId;
    private String cardNumberMasked;
    private String cardBin;
    private String cardType;
    private String provider;
    private LocalDate expiryDate;
    private String status;
    private LocalDateTime issuedAt;
    
    
    public Card() {
    	
    }
    
    
	public Card(Long cardId, Long userId, Long accountId, String cardNumberMasked, String cardBin, String cardType,
			String provider, LocalDate expiryDate, String status, LocalDateTime issuedAt) {
		super();
		this.cardId = cardId;
		this.userId = userId;
		this.accountId = accountId;
		this.cardNumberMasked = cardNumberMasked;
		this.cardBin = cardBin;
		this.cardType = cardType;
		this.provider = provider;
		this.expiryDate = expiryDate;
		this.status = status;
		this.issuedAt = issuedAt;
	}
	
	
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getCardNumberMasked() {
		return cardNumberMasked;
	}
	public void setCardNumberMasked(String cardNumberMasked) {
		this.cardNumberMasked = cardNumberMasked;
	}
	public String getCardBin() {
		return cardBin;
	}
	public void setCardBin(String cardBin) {
		this.cardBin = cardBin;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public LocalDate getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(LocalDateTime issuedAt) {
		this.issuedAt = issuedAt;
	}
    
    
    
    
}
