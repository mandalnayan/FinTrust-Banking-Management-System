package com.fintrust.viewModel;

import com.fintrust.*;
import com.fintrust.model.User;
import com.fintrust.service.OtpService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;

public class OtpViewModel {

	private String email;
	private String otpCode;
	private String statusMessage;
	private final OtpService otpService;
	private final String adminEmail = "nayanm417@gmail.com";
	private final String password = "ackibmuewmmkfydp";

	private boolean success; // css file

	public OtpViewModel() {
		// Initialize manually (since no Spring)

		var repo = new com.fintrust.repository.OtpRepository();
		var mailSender = new com.fintrust.service.MailSenderWrapper("smtp.gmail.com", "587", adminEmail, password);

		otpService = new OtpService(mailSender, repo);
		email = "nayankm99@gmail.com";
		statusMessage = (String) Sessions.getCurrent().getAttribute("statusMessage");
	}

	public boolean isSuccess() {
		return success;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Command
	@NotifyChange({ "statusMessage", "success" })
	public void sendOtp() {
		try {
			if (email == null || email.isBlank()) {
				statusMessage = "Enter a valid email";
				return;
			}
			if (Sessions.getCurrent().getAttribute("userEmail") == null) {
				Sessions.getCurrent().setAttribute("userEmail", email);
			}
			User user = new UserServiceImpl().getUserByUserName(email);
			if (user == null) {
				NotificationUtil.showInstant("warning", "Email doesn't exist. Please enter valid email");
				return;
			}
			otpService.generateAndSendOtp(email);
			Sessions.getCurrent().setAttribute("otp_allowed", true);
			success = true;
			statusMessage = "OTP sent to " + email.substring(0, 3) + email.substring(3).replaceAll("[a-z0-9]", "x");
			Executions.sendRedirect("/auth/verifyOtp.zul");
		} catch (Exception e) {
			statusMessage = "Failed to send OTP: " + e.getMessage();
			e.printStackTrace();
		}
		Sessions.getCurrent().setAttribute("statusMessage", statusMessage);
	}

	@Command
	@NotifyChange({ "statusMessage", "success" })
	public void verifyOtp() {
		if (otpService.verifyOtp(email, otpCode)) {
			success = true;
			Sessions.getCurrent().removeAttribute("otp_allowed");
			Sessions.getCurrent().setAttribute("resetPassword_allowed", true);
			statusMessage = "Verification successful!";

			Executions.sendRedirect("/auth/resetPassword.zul"); // redirect after success
		} else {
			success = false;
			statusMessage = "Invalid or expired OTP";
		}
		Sessions.getCurrent().setAttribute("statusMessage", statusMessage);
	}

	@Command
	public void resendOtp() {
		sendOtp();
	}

	public static void main(String args[]) {
		OtpViewModel om = new OtpViewModel();
		om.sendOtp();
	}
}
