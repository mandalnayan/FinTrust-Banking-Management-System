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
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.lang.Library;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.fintrust.model.UserDetails;
import com.fintrust.model.UserDocument;
import com.fintrust.exception.NetworkUnavailableException;
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
	private String addressFilePath;

	private byte[] addressProofFile;
	Set<String> addressType = Set.of("application/pdf");

	private String photoLabel;
	private byte[] photoFile;
	private String photoFilePath;

	private String statusMessage;
	private String otpCode;
	Set<String> photoType = Set.of("image/jpeg", "image/png");
	private UserServiceImpl userService = new UserServiceImpl();
	private OtpService otpService;
	private UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();

	// Directory where files will be saved
	String photoUploadDir = "/resources/uploads/photo/"; // change as needed
	String pdfUploadDir = "/resources/uploads/addressProof/"; // change as needed

//	File uploaded
	UserDocument photoDoc, addressProofDoc;

	private static final Logger logger = LogManager.getLogger(KycFormVM.class);

	@Init
	public void init() {
		// Load logged-in user's KYC details
		userDetails = userDetailsService.getLogedInDetails();

		if (userDetails == null) {
			NotificationUtil.showInstant("error", "Server error. Failed to load userdetails");
			return;
		}

		otpService = new OtpService();
		addressProofDoc = new UserDocument();
		photoDoc = new UserDocument();
		addressProofDoc.setStoredFileName(userDetails.getAddressProofFileName());
		photoDoc.setStoredFileName(userDetails.getPhotoFileName());

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
		String fileName = userKycDTO.getAddressProofFileName();
		if (fileName == null || fileName.isBlank()) {
			return "";
		}
		return fileName.substring(fileName.indexOf('$') + 1);
	}

	public String getPhotoLabel() {
		String fileName = userKycDTO.getPhotoFileName();
		if (fileName == null || fileName.isBlank()) {
			return "";
		}
		return fileName.substring(fileName.indexOf('$') + 1);
	}

	public String getAddressProofView() {
		String addressPath = "addressProofView";
		String addressProof = "/" + addressPath + ".zul";

		return addressProof;
	}

	/**
	 * Get file path
	 * 
	 * @return
	 */
	public String getPhotoFilePath() {
		return photoUploadDir + photoLabel;
	}

	/**
	 * Get file path
	 * 
	 * @return
	 */
	public String getAddressFilePath() {
		return pdfUploadDir + addressProofLabel;
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
		// System.out.println("Updating..");
	}

	// --------------------------
	// FILE UPLOAD
	// --------------------------
	@Command
	@NotifyChange("*")
	public void uploadAddressProof(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {

		Media addressMedia = event.getMedia();
		String fileName = addressMedia.getName();

		addressProofDoc.setMedia(addressMedia);
		addressProofDoc.setStoragePath(pdfUploadDir);
		addressProofDoc.setDocType(addressMedia.getContentType());
		addressProofDoc.setOriginalFileName(fileName);
		Messagebox.show(addressMedia.getContentType() + " pdf file");
		try {
			// validate file (existing logic)
			validateFile(addressMedia, "upload.address.max.size", addressType, "Address proof must be less than 5 MB");

			// File name

			addressProofLabel = fileName;
			userKycDTO.setAddressProofFileName(fileName);

			NotificationUtil.showInstant("success", "File uploaded successfully $");

		} catch (WrongValueException ex) {
			NotificationUtil.showInstant("warning", ex.getMessage());
			logger.error("File format or size is not supported", ex);
		}
	}

	@Command
	@NotifyChange("*")
	public void uploadPhoto(@org.zkoss.bind.annotation.BindingParam("event") UploadEvent event) {
		Media photoMedia = event.getMedia();
		String fileName = photoMedia.getName();

		photoDoc.setMedia(photoMedia);
		photoDoc.setStoragePath(photoUploadDir);
		photoDoc.setDocType(photoMedia.getContentType());
		photoDoc.setOriginalFileName(fileName);
		Messagebox.show(photoMedia.getContentType() + " image file");
		try {
			validateFile(photoMedia, "upload.photo.max.size", photoType, "Photo must be less than 50 KB");
			photoFile = photoMedia.getByteData();

			photoLabel = fileName;
			userKycDTO.setPhotoFileName(fileName);

			NotificationUtil.showInstant("success", "File uploaded successfully");
		} catch (WrongValueException ex) {
			NotificationUtil.showInstant("warning", ex.getMessage());
			logger.error("File format or size is not supported", ex);
		}
	}

	@Command
	@NotifyChange("photoFilePath")
	public void viewPhoto() {
		Window win = (Window) Executions.createComponents("/user/documents/image_preview_frame.zul", null, null);
		win.doModal();
	}

	@Command
	@NotifyChange("addressFilePath")
	public void viewPDF() throws IOException {
		Window win = (Window) Executions.createComponents("/user/documents/pdf_preview_frame.zul", null, null);
		win.doModal();

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
		entity.setAddressProofFileName(addressProofDoc.getStoredFileName());
		entity.setPhotoFileName(photoDoc.getStoredFileName());

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

		if (dto.getPincode() == null || !dto.getPincode().matches("[0-9]{6}$"))
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
			Session session = Sessions.getCurrent();
			session.setAttribute("userDetails", userDetails);
			session.setAttribute("otpService", otpService);
			session.setAttribute("photoDoc", photoDoc);
			session.setAttribute("addressDoc", addressProofDoc);

			Window win = (Window) Executions.createComponents("/WEB-INF/components/otpAuthentication.zul", null, null);
			win.doModal();
		} catch (NetworkUnavailableException e) {
			NotificationUtil.showInstant("error", "You're not connected to the internet. Please check your network.",
					6000);
			e.printStackTrace();
		} catch (MessagingException e) {
			NotificationUtil.showInstant("error", "Unable to send OTP. Please try again later.", 6000);
			e.printStackTrace();
		}
	}
}
