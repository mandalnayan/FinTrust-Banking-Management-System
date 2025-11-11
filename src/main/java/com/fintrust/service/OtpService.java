package com.fintrust.service;


	import java.time.Instant;
	import java.time.temporal.ChronoUnit;
	import java.util.concurrent.ThreadLocalRandom;

import com.fintrust.model.OtpRecord;
import com.fintrust.repository.OtpRepository;

import jakarta.mail.MessagingException;

	public class OtpService {

	    private final MailSenderWrapper mailSender;
	    private final OtpRepository otpRepository;

	    public OtpService(MailSenderWrapper mailSender, OtpRepository otpRepository) {
	        this.mailSender = mailSender;
	        this.otpRepository = otpRepository;
	    }

	    public void generateAndSendOtp(String email) throws MessagingException {
	        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
	        Instant expiry = Instant.now().plus(5, ChronoUnit.MINUTES);

	        otpRepository.saveOtpForEmail(email, code, expiry);

	        mailSender.sendSimple(email, "Your OTP Code", "Your OTP code is: " + code + "\nIt expires in 5 minutes.");
	    }

	    public boolean verifyOtp(String email, String code) {
	        OtpRecord rec = otpRepository.findLatestForEmail(email);
	        if (rec == null || rec.isUsed() || rec.getExpiry().isBefore(Instant.now()) || !rec.getCode().equals(code))
	            return false;

	        rec.setUsed(true);
	        otpRepository.update(rec);
	        return true;
	    }
}


