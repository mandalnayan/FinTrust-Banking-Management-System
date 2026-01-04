package com.fintrust.model;



import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminKycDTO {

    private int kycId;
    private int userId;

    private String fullName;
    private String maskedPhone;
    private String email;

    private LocalDate dob;
    private String gender;
    private String maskedAadhaar;
    private String maskedPan;

    private String country;
    private String state;
    private String district;
    private String city;
    private String pincode;

    private String photoFileName;
    private String addressProofFileName;

    private String kycStatus;
    private String adminRemarks;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
	public int getKycId() {
		return kycId;
	}
	public void setKycId(int kycId) {
		this.kycId = kycId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getMaskedPhone() {
		return maskedPhone;
	}
	public void setMaskedPhone(String maskedPhone) {
		this.maskedPhone = maskedPhone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMaskedAadhaar() {
		return maskedAadhaar;
	}
	public void setMaskedAadhaar(String maskedAadhaar) {
		this.maskedAadhaar = maskedAadhaar;
	}
	public String getMaskedPan() {
		return maskedPan;
	}
	public void setMaskedPan(String maskedPan) {
		this.maskedPan = maskedPan;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getPhotoFileName() {
		return photoFileName;
	}
	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}
	public String getAddressProofFileName() {
		return addressProofFileName;
	}
	public void setAddressProofFileName(String addressProofFileName) {
		this.addressProofFileName = addressProofFileName;
	}
	public String getKycStatus() {
		return kycStatus;
	}
	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}
	public String getAdminRemarks() {
		return adminRemarks;
	}
	public void setAdminRemarks(String adminRemarks) {
		this.adminRemarks = adminRemarks;
	}
	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}
	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}
	public LocalDateTime getVerifiedAt() {
		return verifiedAt;
	}
	public void setVerifiedAt(LocalDateTime verifiedAt) {
		this.verifiedAt = verifiedAt;
	}
	public String getVerifiedBy() {
		return verifiedBy;
	}
	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}
	@Override
	public String toString() {
		return "AdminKycDTO [kycId=" + kycId + ", userId=" + userId + ", fullName=" + fullName + ", maskedPhone="
				+ maskedPhone + ", email=" + email + ", dob=" + dob + ", gender=" + gender + ", maskedAadhaar="
				+ maskedAadhaar + ", maskedPan=" + maskedPan + ", country=" + country + ", state=" + state
				+ ", district=" + district + ", city=" + city + ", pincode=" + pincode + ", photoFileName="
				+ photoFileName + ", addressProofFileName=" + addressProofFileName + ", kycStatus=" + kycStatus
				+ ", adminRemarks=" + adminRemarks + ", submittedAt=" + submittedAt + ", verifiedAt=" + verifiedAt
				+ ", verifiedBy=" + verifiedBy + "]";
	}



}