package com.fintrust.model_copy;

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

	@Override
	public String toString() {
		return "OtpRecord [email=" + email + ", code=" + code + ", expiry=" + expiry + ", used=" + used + "]";
	}
    
    
}