package com.fintrust.viewModel;

import com.fintrust.*;
import com.fintrust.service.OtpService;

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
    private final String password = "zxrhrgrielkhqxml";
    
    private boolean success; // css file

    public OtpViewModel() {
        // Initialize manually (since no Spring)
        
        var repo = new com.fintrust.repository.OtpRepository();
        var mailSender = new com.fintrust.service.MailSenderWrapper(
            "smtp.gmail.com", "587", adminEmail, password);

        otpService = new OtpService(mailSender, repo);
    }
    
    public boolean isSuccess() { return success; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
   

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    @Command
    @NotifyChange({"statusMessage", "success"})
    public void sendOtp() {
        try {
            if (email == null || email.isBlank()) {
                statusMessage = "Enter a valid email";               
                return;
            }
            otpService.generateAndSendOtp(email);
            success = true;
            statusMessage = "OTP sent to " + email;
        } catch (Exception e) {
            statusMessage = "Failed to send OTP: " + e.getMessage();
        }
    }

    @Command
    @NotifyChange({"statusMessage", "success"})
    public void verifyOtp() {
        if (otpService.verifyOtp(email, otpCode)) {
        		success = true;
        		Sessions.getCurrent().setAttribute("currentUser", email);
            statusMessage = "Verification successful!";
            Executions.sendRedirect("/changePassword.zul"); // redirect after success
        } else {
        		success = false;
            statusMessage = "Invalid or expired OTP";
        }
    }
}

