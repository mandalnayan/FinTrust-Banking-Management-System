package com.fintrust.viewModel;

import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.annotation.Command;

import com.fintrust.model.UserDetails;
import com.fintrust.service.OtpService;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.util.NotificationUtil;

import jakarta.mail.MessagingException;

public class OtpVerificationVM {

    private String otpCode;
    private String statusMessage;
    private OtpService otpService;
    private UserDetails userDetails;
    private UserDetailsServiceImpl userDetailsService;

    @Init
    public void init() {
        otpService = (OtpService) Sessions.getCurrent().getAttribute("otpService");
        userDetails = (UserDetails) Sessions.getCurrent().getAttribute("userDetails");
        statusMessage = (String) Sessions.getCurrent().getAttribute("statusMessage");
        userDetailsService = new UserDetailsServiceImpl();
    }

    @Command
	@NotifyChange({ "statusMessage", "success" })
	public void verifyOtp() {
		
		if (otpService.verifyOtp(userDetails.getUser().getEmail(), otpCode)) {		
	        System.out.println("submitting-1 " + userDetails);	        

			statusMessage = "Verification successful!";			
			boolean updated = userDetailsService.updateKyc(userDetails);
	        if (updated) {
	            NotificationUtil.push("info", "KYC submitted successfully!");
	            Executions.sendRedirect("");
	        } else {
	            NotificationUtil.showInstant("error", "Failed to save KYC details!");
	        }
			
		} else {
			statusMessage = "Invalid or expired OTP";
		}
		Sessions.getCurrent().setAttribute("statusMessage", statusMessage);
	}

    @Command
    public void resendOtp() {
        try {
			otpService.generateAndSendOtp(userDetails.getUser().getEmail());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }

    // getters/setters
    public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}
	
	public String getStatusMessage() {
		return this.statusMessage;
	}
}

