package com.fintrust.model;

import java.time.LocalDate;
import javax.validation.constraints.*;

public class UserKycDTO {

    // USER BASIC DETAILS
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    // KYC DETAILS
    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Aadhaar number required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar must be 12 digits")
    private String aadhaarNumber;

    @NotBlank(message = "PAN number required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String panNumber;

    @NotBlank(message = "Country required")
    private String country;

    @NotBlank(message = "State required")
    private String state;

    @NotBlank(message = "District required")
    private String district;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "Pincode required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    private String pincode;
    
 // getters & setters ...
    
    private String photoFileName;
    private String addressProofFileName;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
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

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getAadhaarNumber() {
		return aadhaarNumber;
	}

	public void setAadhaarNumber(String aadhaarNumber) {
		this.aadhaarNumber = aadhaarNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
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

	@Override
	public String toString() {
		return "UserKycDTO [fullName=" + fullName + ", phone=" + phone + ", email=" + email + ", dob=" + dob
				+ ", gender=" + gender + ", aadhaarNumber=" + aadhaarNumber + ", panNumber=" + panNumber + ", country="
				+ country + ", state=" + state + ", district=" + district + ", city=" + city + ", pincode=" + pincode
				+ ", photoFileName=" + photoFileName + ", addressProofFileName=" + addressProofFileName + "]";
	}    
    
}
