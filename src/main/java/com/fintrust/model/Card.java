package com.fintrust.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Card {

    private Long cardId;
    private Long userId;
    private Long accountId;

    private String cardNumberMasked;   // XXXX-XXXX-XXXX-1234
    private String last4;              // last 4 digits only

    private String pinHash;            // encrypted PIN

    private LocalDate issuedDate;
    private LocalDate expiryDate;

    private String cardStatus;         // active, blocked, expired, hotlisted
    private String provider;           // visa, mastercard, rupay, amex

    private BigDecimal maximumLimit;   // credit/prepaid limit

    private LocalDateTime createdAt;

    public Card() {}

    public Card(Long cardId, Long userId, Long accountId, String cardNumberMasked, String last4,
                String pinHash, LocalDate issuedDate, LocalDate expiryDate, String cardStatus,
                String provider, BigDecimal maximumLimit, LocalDateTime createdAt) {

        this.cardId = cardId;
        this.userId = userId;
        this.accountId = accountId;
        this.cardNumberMasked = cardNumberMasked;
        this.last4 = last4;
        this.pinHash = pinHash;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.cardStatus = cardStatus;
        this.provider = provider;
        this.maximumLimit = maximumLimit;
        this.createdAt = createdAt;
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

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public BigDecimal getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(BigDecimal maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
