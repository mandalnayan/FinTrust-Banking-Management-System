package com.fintrust.viewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Messagebox;

import com.fintrust.model.User;
import com.fintrust.model.UserDetails;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

public class KYCFormVM {

    private UserDetails userDetails;
    private User user;

    private byte[] addressProofFile;
    private byte[] photoFile;
    private Date dob;

    private UserServiceImpl userService = new UserServiceImpl();
    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();

    @Init
    public void init() {
        userDetails = userDetailsService.getLogedInDetails();
        System.out.println(userDetails);
        user = userDetails.getUser();
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public User getUser() {
        return user;
    }
    
    /**
     * Getting DOB date in specific formate(dd-MM-YYY)
     * @return
     */
    public Date getDob() {
        if (userDetails == null
                || userDetails.getDob() == null) {
            return null;
        }  


        LocalDate localDate = userDetails.getDob();

        dob = Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        
        return dob;
    }
    
    /**
     * Setting DOB date in specific formate(dd-MM-YYY)
     * @return
     */
    @NotifyChange("dob")
    public void setDob(Date dob) {
    		System.out.println("Hii, updating dob");
        if (userDetails == null
                || dob == null) {
            return ;
        }  
       
        LocalDate newDob = dob.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        System.out.println("Hii, updating dob" + newDob);
        userDetails.setDob(newDob);
        NotificationUtil.showInstant("info", newDob.toString());
      
    }

    // --------------------------
    // FILE UPLOAD
    // --------------------------
    @Command
    @NotifyChange("*")
    public void uploadAddressProof(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
        addressProofFile = event.getMedia().getByteData();
        userDetails.setAddressProof(event.getMedia().getName());
    }

    @Command
    @NotifyChange("*")
    public void uploadPhoto(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
        photoFile = event.getMedia().getByteData();
        userDetails.setPhoto(event.getMedia().getName());
    }

    // --------------------------
    // SUBMIT KYC
    // --------------------------
    @Command
    public void submitKyc() {
    		
        boolean updated = userDetailsService.updateKyc(userDetails);

        if (updated) {
           	NotificationUtil.showInstant("info", "KYC submitted successfully!");
        } else {
        	NotificationUtil.showInstant("error", "Failed to save KYC details!");
        }
    }

    // --------------------------
    // VALIDATION
    // --------------------------
    public Validator getKycValidator() {

        return ctx -> {

            String name = (String) ctx.getProperties("user.fullName")[0].getValue();
            String phone = (String) ctx.getProperties("user.phone")[0].getValue();
            String email = (String) ctx.getProperties("user.email")[0].getValue();

            Date dob = (Date) ctx.getProperties("userDetails.dob")[0].getValue();

            String aadhar = (String) ctx.getProperties("aadhaarMasked")[0].getValue();
            String pan = (String) ctx.getProperties("panMasked")[0].getValue();

            if (name == null || name.isEmpty())
                addError(ctx, "user.fullName", "Full Name is required");

            if (phone == null || !phone.matches("^[0-9]{10}$"))
                addError(ctx, "user.phone", "Enter a valid 10-digit mobile");

            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                addError(ctx, "user.email", "Enter valid email");

            if (dob == null)
                addError(ctx, "userDetails.dob", "Date of Birth required");

            if (aadhar == null || !aadhar.matches("^[0-9]{12}$"))
                addError(ctx, "aadhaarMasked", "Enter valid 12-digit Aadhar");

            if (pan == null || !pan.matches("[A-Z]{5}[0-9]{4}[A-Z]"))
                addError(ctx, "panMasked", "Enter valid PAN");
        };
    }

    private void addError(ValidationContext ctx, String field, String message) {
        ctx.setInvalid();
    //    ctx.getInvalidMessages().addFieldError(field, message);
    }
}
