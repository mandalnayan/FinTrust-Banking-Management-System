package com.fintrust.service;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.fintrust.exception.ServerException;
import com.fintrust.util.NotificationUtil;

import jakarta.mail.MessagingException;

/**
 * Future implementation
 */

public class OtpAuthentication extends SelectorComposer<Window>{

	private static String email;
	private static String otpCode;
	private static String message;
	private static OtpService otpService;
	private static final String adminEmail = "nayanm417@gmail.com";
	private final static String password = "ackibmuewmmkfydp";
	
	@Wire Label statusMessage;
	
	public static boolean otpAuthentication() {
		try {
//	     	Preparing otp serivce
			prepareOtpService();
			
			// sending email
			otpService.generateAndSendOtp(email);
			
			/* statusMessage.setValue(); */
			int emailLen = email.length();
			message = "OTP sent to " + email.substring(0, emailLen-3).replaceAll("[a-z0-9]", "x") + email.substring(emailLen-3);
			System.out.println(message);
		} catch (ServerException | MessagingException e) {	
			NotificationUtil.showInstant("error", e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static void prepareOtpService() throws ServerException {
		var repo = new com.fintrust.repository.OtpRepository();
		var mailSender = new com.fintrust.service.MailSenderWrapper("smtp.gmail.com", "587", adminEmail, password);
		email = (String) Sessions.getCurrent().getAttribute("user_email");
		if (email == null) throw new ServerException("Server error");
		otpService = new OtpService(mailSender, repo);
	}
}
