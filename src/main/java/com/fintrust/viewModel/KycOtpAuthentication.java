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

public class KycOtpAuthentication {

	private String email;
	private String otpCode;
	private String statusMessage;
	private final OtpService otpService;

	private boolean success; // css file

// Initialize manually (since no Spring)
	public KycOtpAuthentication() {

		otpService = new OtpService();
		email = (String) Sessions.getCurrent().getAttribute("userEmail");
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
				Executions.sendRedirect("/user/login.zul");
			}
			User user = new UserServiceImpl().getUserByUserName(email);
			if (user == null) {
				NotificationUtil.showInstant("warning", "Email doesn't exist. Please enter valid email");
				return;
			}
			otpService.generateAndSendOtp(email);
			Sessions.getCurrent().setAttribute("otp_allowed", true);
			success = true;
			int emailLen = email.length();
			statusMessage = "OTP sent to " + email.substring(0, emailLen - 3).replaceAll("[a-z0-9]", "x")
					+ email.substring(emailLen - 3);
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
		KycOtpAuthentication om = new KycOtpAuthentication();
		om.sendOtp();
	}
}
