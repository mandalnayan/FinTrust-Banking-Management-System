package com.fintrust.viewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Library;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

import com.fintrust.model.UserDetails;
import com.fintrust.model.User;
import com.fintrust.model.UserKycDTO;
import com.fintrust.service.OtpService;
import com.fintrust.service.UserDetailsServiceImpl;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

import jakarta.mail.MessagingException;

public class KycFormVM {

	private UserKycDTO userKycDTO = new UserKycDTO(); // DTO for form binding
	private UserDetails userDetails;
	private User user;

	private String addressProofLabel;
	private String addressProofView;
	
	private byte[] addressProofFile;
	Set<String> addressType = Set.of("application/pdf");

	private String photoLabel;
	private byte[] photoFile;
	private String statusMessage;
	private String otpCode;
	Set<String> photoType = Set.of("image/jpeg", "image/png");
	private UserServiceImpl userService = new UserServiceImpl();
	private OtpService otpService;
	private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
	
	// Directory where files will be saved
    String uploadDir = "/src/main/webapp/WEB-INF/resources/uploads/address-proof/"; // change as needed

	private static final Logger logger = LogManager.getLogger(KycFormVM.class);

	@Init
	public void init() {
		// Load logged-in user's KYC details
		userDetails = userDetailsService.getLogedInDetails();
		otpService = new OtpService();
		if (userDetails == null) {
			NotificationUtil.showInstant("error", "Server error. Failed to load userdetails");
			return;
		}

		user = userDetails.getUser();

		// Map entity to DTO
		userKycDTO = mapEntityToDTO(userDetails);
		addressProofLabel = userKycDTO.getAddressProofFileName();
		photoLabel = userKycDTO.getPhotoFileName();
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
		userKycDTO.setGender(gender);
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public String getAddressProofLabel() {
		if (userKycDTO.getAddressProofFileName() == null || userKycDTO.getAddressProofFileName().isBlank()) {
			return "Upload address";
		}
		return userKycDTO.getAddressProofFileName();
	}

	public String getPhotoLabel() {	
		if (userKycDTO.getPhotoFileName() == null || userKycDTO.getPhotoFileName().isBlank()) {
			return "Upload photo";
		}
		return userKycDTO.getPhotoFileName();
	}
	
	public String getAddressProofView() {
		System.out.println("invoked photo " + userKycDTO.getPhotoFileName());
		String addressPath = "addressProofView";
		String addressProof = "/" + addressPath + ".zul";
		
		return addressProof;
	}

	/**
	 * DOB conversion for ZK datebox (java.util.Date)
	 */
	public Date getDob() {
		if (userKycDTO.getDob() == null)
			return null;
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
	public void uploadAddressProof(
	        @org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {

	    Media media = event.getMedia();

	    try {
	        // validate file (existing logic)
	        validateFile(media, "upload.address.max.size", addressType,
	                "Address proof must be less than 5 MB");

	        // File name
	        String fileName = media.getName();
	        addressProofLabel = fileName;
	        userKycDTO.setAddressProofFileName(fileName);

	        File dir = new File(uploadDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }

	        // Target file
	        File file = new File(uploadDir + File.separator + fileName);

	        // Save file
	        if (media.isBinary()) {
	            try (FileOutputStream fos = new FileOutputStream(file)) {
	                fos.write(media.getByteData());
	            }
	        } else {
	            // For text-based media
	            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
	                writer.write(media.getStringData());
	            }
	        }

	        NotificationUtil.showInstant("success", "File uploaded successfully");

	    } catch (WrongValueException ex) {
	        NotificationUtil.showInstant("warning", ex.getMessage());
	        logger.error("File format or size is not supported", ex);
	    } catch (IOException ex) {
	        NotificationUtil.showInstant("error", "Failed to save file" + ex.getMessage());
	        logger.error("File saving failed", ex);
	    }
	}

	@Command
	@NotifyChange("*")
	public void uploadPhoto(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
		Media media = event.getMedia();

		try {
			validateFile(media, "upload.photo.max.size", photoType, "Photo must be less than 50 KB");
			photoFile = media.getByteData();
			photoLabel = media.getName();
			String type = media.getContentType();
			userKycDTO.setPhotoFileName(photoLabel);
			
//			Saving into local file system
			File dir = new File(uploadDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        
	     // Target file
	        File file = new File(uploadDir + File.separator + photoLabel);

	        // Save file
	        if (media.isBinary()) {
	            try (FileOutputStream fos = new FileOutputStream(file)) {
	                fos.write(media.getByteData());
	            }
	        } else {
	            // For text-based media
	            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
	                writer.write(media.getStringData());
	            }
	        }
			
			NotificationUtil.showInstant("warning", "size " + photoFile.length + " type " + type);
		 } catch (WrongValueException ex) {
		        NotificationUtil.showInstant("warning", ex.getMessage());
		        logger.error("File format or size is not supported", ex);
		    } catch (IOException ex) {
		        NotificationUtil.showInstant("error", "Failed to save file" + ex.getMessage());
		        logger.error("File saving failed", ex);
		    }

	}
	
	@Command
	public void viewPhoto() {
	    String fileName = "airtle2.png";

	    File file = new File(fileName);
	    if (!file.exists()) {
	        Messagebox.show(
	            "File not found: " + file.getAbsolutePath(),
	            "Missing File",
	            Messagebox.OK,
	            Messagebox.EXCLAMATION
	        );
	        return;
	    }

	    try {
	        AMedia media = new AMedia(file, null, null);

	        Map<String, Object> args = new HashMap<>();
	        args.put("pdfContent", media);

	        Window win = (Window) Executions.createComponents(
	            "/user/documents/pdf_preview_frame.zul",
	            null,
	            args
	        );
	        win.doModal();

	    } catch (Exception e) {
	        Messagebox.show("Error opening file: " + e.getMessage());
	    }
	}


	// --------------------------
	// SUBMIT KYC
	// --------------------------
	@Command
	public void submitKyc() {
		// Validate manually
		String validationError = validateKycDTO(userKycDTO);
		if (validationError != null) {
			NotificationUtil.showInstant("error", validationError);
			return;
		}

		// Map DTO back to entity
		userDetails = mapDTOToEntity(userKycDTO, userDetails);

//        OTP verification before submitting kyc        
		sendOtp();
	}

	// --------------------------
	// UTILITY: Mapping Methods
	// --------------------------
	private UserKycDTO mapEntityToDTO(UserDetails entity) {
		UserKycDTO dto = new UserKycDTO();
		if (entity == null)
			return dto;

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
		dto.setAddressProofFileName(entity.getAddressProofFileName());
		dto.setPhotoFileName(entity.getPhotoFileName());
		return dto;
	}

	private UserDetails mapDTOToEntity(UserKycDTO dto, UserDetails entity) {
		if (entity == null)
			entity = new UserDetails();
		if (entity.getUser() == null)
			entity.setUser(user);

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
		entity.setAddressProofFileName(addressProofLabel);
		entity.setPhotoFileName(photoLabel);

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
		if (addressProofLabel == null)
			return "address proof is required";
		if (photoLabel == null)
			return "Photo must be required";

		return null;
	}

	/**
	 * Validate file
	 * 
	 * @param media
	 * @param propertyKey
	 * @param allowedTypes
	 * @param errorMsg
	 */
	private void validateFile(Media media, String maxSizeKey, Set<String> allowedTypes, String errorMsg) {

		long maxSize = Long.parseLong(Library.getProperty(maxSizeKey));

// Size check
		if (media.getByteData().length > maxSize) {
			throw new WrongValueException(errorMsg);
		}

// Type check (MIME)
		if (!allowedTypes.contains(media.getContentType())) {
			throw new WrongValueException("Invalid file type");
		}
	}

	/*
	 * OTP authentication
	 */
	public void sendOtp() {
		try {
			otpService.generateAndSendOtp(user.getEmail());

			Sessions.getCurrent().setAttribute("userDetails", userDetails);
			Sessions.getCurrent().setAttribute("otpService", otpService);

			Include inc = (Include) Sessions.getCurrent().getAttribute("main_content_sec");
			inc.setSrc("/WEB-INF/components/otpAuthentication.zul");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
