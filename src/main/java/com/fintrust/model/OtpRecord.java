package com.fintrust.model;

import java.time.Instant;

public class OtpRecord {
    private String email;
    private String code;
    private Instant expiry;
    private boolean used;

    public OtpRecord(String email, String code, Instant expiry) {
        this.email = email;
        this.code = code;
        this.expiry = expiry;
        this.used = false;
    }

    public String getEmail() { return email; }
    public String getCode() { return code; }
    public Instant getExpiry() { return expiry; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}