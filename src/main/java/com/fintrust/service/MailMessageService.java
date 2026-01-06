package com.fintrust.service;

import com.sun.mail.util.MailConnectException;

import com.fintrust.exception.NetworkUnavailableException;

import com.fintrust.repository.OtpRepository;
import com.fintrust.util.NotificationUtil;

import jakarta.mail.MessagingException;

public class MailMessageService {

	private final MailSenderWrapper mailSender;
	private final String adminEmail = "nayanm417@gmail.com";
	private final String password = "ackibmuewmmkfydp";

	/**
	 * Default initialization
	 */
	public MailMessageService() {
		this.mailSender = new com.fintrust.service.MailSenderWrapper("smtp.gmail.com", "587", adminEmail, password);
		
	}

	public MailMessageService(MailSenderWrapper mailSender, OtpRepository otpRepository) {
		this.mailSender = mailSender;
		
	}

	public void generateAndSendMessage(String email, String subject, String message) throws MessagingException {
			
		
		try {
			mailSender.sendSimple(email, subject, message);
		} catch (MailConnectException e) {
			NotificationUtil.showInstant("warning", "Failed to send email to user");
			 throw new NetworkUnavailableException(e);
		}
	}	
}
