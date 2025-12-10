package com.fintrust.viewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Radio;

import com.fintrust.model.UserDetails;
import com.fintrust.model.User;
import com.fintrust.model.UserKycDTO;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

public class KycFormVM {

    private UserKycDTO userKycDTO = new UserKycDTO(); // DTO for form binding
    private UserDetails userDetails;
    private User user;
        
    private byte[] addressProofFile;
    private byte[] photoFile;
    

    private UserServiceImpl userService = new UserServiceImpl();
    private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();  
   
    @Init
    public void init() {
        // Load logged-in user's KYC details
        userDetails = userDetailsService.getLogedInDetails();
        if (userDetails == null) {
        	NotificationUtil.showInstant("error", "Server error. Failed to load userdetails");            
            return;
        }     
       
        user = userDetails.getUser();
        System.out.println(userDetails);

        // Map entity to DTO
        userKycDTO = mapEntityToDTO(userDetails);
        System.out.println(userKycDTO);
        
    }

    public UserKycDTO getUserKycDTO() {
        return userKycDTO;
    }

    public User getUser() {
        return user;
    }
    
    public String getGender() {
        return userKycDTO.getGender();
    }

    public void setGender(String gender) {
    	System.out.println("Updating gender");
        userKycDTO.setGender(gender);
    }
    
    /**
     * DOB conversion for ZK datebox (java.util.Date)
     */
    public Date getDob() {
        if (userKycDTO.getDob() == null) return null;
        return Date.from(userKycDTO.getDob().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @NotifyChange("dob")
    public void setDob(Date dob) {
        if (dob != null) {
            LocalDate localDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            userKycDTO.setDob(localDate);
        }
    }
    
    // --------------------------
    // Gender changed
    // --------------------------
    @NotifyChange("userKycDTO.gender")
    public void updateGender() {
    	System.out.println("Updating..");
    }

    
    // --------------------------
    // FILE UPLOAD
    // --------------------------
    @Command
    @NotifyChange("*")
    public void uploadAddressProof(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
        addressProofFile = event.getMedia().getByteData();
        userKycDTO.setAddressProofFileName(event.getMedia().getName());
    }

    @Command
    @NotifyChange("*")
    public void uploadPhoto(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
        photoFile = event.getMedia().getByteData();
        userKycDTO.setPhotoFileName(event.getMedia().getName());
    }

    // --------------------------
    // SUBMIT KYC
    // --------------------------
    @Command
    public void submitKyc() {
        // Validate manually (can also use Hibernate Validator programmatically)
        String validationError = validateKycDTO(userKycDTO);
        if (validationError != null) {
            NotificationUtil.showInstant("error", validationError);
            return;
        }

        // Map DTO back to entity
        userDetails = mapDTOToEntity(userKycDTO, userDetails);
        System.out.println("submitting " + userDetails);

        boolean updated = userDetailsService.updateKyc(userDetails);
        if (updated) {
            NotificationUtil.showInstant("info", "KYC submitted successfully!");
        } else {
            NotificationUtil.showInstant("error", "Failed to save KYC details!");
        }
    }

    // --------------------------
    // UTILITY: Mapping Methods
    // --------------------------
    private UserKycDTO mapEntityToDTO(UserDetails entity) {
        UserKycDTO dto = new UserKycDTO();
        if (entity == null) return dto;

        if (entity.getUser() != null) {
            dto.setFullName(entity.getUser().getFullName());
            dto.setPhone(entity.getUser().getPhone());
            dto.setEmail(entity.getUser().getEmail());
        }

        dto.setDob(entity.getDob());
        if (entity.getGender() != null) {
            dto.setGender(entity.getGender());
        }
        dto.setAadhaarNumber(entity.getAadhaarMasked());
        dto.setPanNumber(entity.getPanMasked());
        dto.setCountry(entity.getCountry());
        dto.setState(entity.getState());
        dto.setDistrict(entity.getDistrict());
        dto.setCity(entity.getCity());
        dto.setPincode(entity.getPincode());

        return dto;
    }

    private UserDetails mapDTOToEntity(UserKycDTO dto, UserDetails entity) {
        if (entity == null) entity = new UserDetails();
        if (entity.getUser() == null) entity.setUser(user);

        entity.getUser().setFullName(dto.getFullName());
        entity.getUser().setPhone(dto.getPhone());
        entity.getUser().setEmail(dto.getEmail());

        entity.setDob(dto.getDob());
        entity.setGender(dto.getGender());
        entity.setAadhaarMasked(dto.getAadhaarNumber());
        entity.setPanMasked(dto.getPanNumber());
        entity.setCountry(dto.getCountry());
        entity.setState(dto.getState());
        entity.setDistrict(dto.getDistrict());
        entity.setCity(dto.getCity());
        entity.setPincode(dto.getPincode());

        return entity;
    }

    // --------------------------
    // Manual Validation (alternative to Hibernate Validator in ZK 10)
    // --------------------------
    private String validateKycDTO(UserKycDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty())
            return "Full Name is required";

        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^[0-9]{10}$"))
            return "Phone must be 10 digits";

        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            return "Invalid email";

        if (dto.getDob() == null)
            return "Date of Birth required";

        if (dto.getGender() == null || dto.getGender().trim().isEmpty())
            return "Gender is required";

        if (dto.getAadhaarNumber() == null || !dto.getAadhaarNumber().matches("^[0-9]{12}$"))
            return "Aadhaar must be 12 digits";

        if (dto.getPanNumber() == null || !dto.getPanNumber().matches("^[A-Z]{5}[0-9]{4}[A-Z]$"))
            return "Invalid PAN format";

        if (dto.getCountry() == null || dto.getCountry().trim().isEmpty())
            return "Country is required";

        if (dto.getState() == null || dto.getState().trim().isEmpty())
            return "State is required";

        if (dto.getDistrict() == null || dto.getDistrict().trim().isEmpty())
            return "District is required";

        if (dto.getCity() == null || dto.getCity().trim().isEmpty())
            return "City is required";

        if (dto.getPincode() == null || !dto.getPincode().matches("^[0-9]{6}$"))
            return "Pincode must be 6 digits";

        return null;
    }

}
