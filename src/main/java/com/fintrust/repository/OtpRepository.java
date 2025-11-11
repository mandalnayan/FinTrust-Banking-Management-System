package com.fintrust.repository;	

	import java.util.concurrent.ConcurrentHashMap;

import com.fintrust.model.OtpRecord;

	public class OtpRepository {

	    private final ConcurrentHashMap<String, OtpRecord> store = new ConcurrentHashMap<>();

	    public void saveOtpForEmail(String email, String code, java.time.Instant expiry) {
	        store.put(email, new OtpRecord(email, code, expiry));
	    }

	    public OtpRecord findLatestForEmail(String email) {
	        return store.get(email);
	    }

	    public void update(OtpRecord rec) {
	        store.put(rec.getEmail(), rec);
	    }
	}

