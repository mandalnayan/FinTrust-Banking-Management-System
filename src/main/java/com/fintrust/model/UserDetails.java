package com.fintrust.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDetails {
    private Long detailsId;
    private User user;
    private Long primaryAccountId;
    private String gender;
    private LocalDate dob;
    private String aadhaarMasked;
    private String panMasked;
    private String country;
    private String state;
    private String district;
    private String city;
    private String pincode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // File paths or blob IDs
    private String addressProofFileName;
    private String photoFileName;

    public UserDetails() {
    		user = new User();
    }

	public UserDetails(Long detailsId, Long userId, String gender, LocalDate dob, String aadhaarMasked,
			String panMasked, String country, String state, String district, String city, String pincode,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.detailsId = detailsId;
		this.user = user;
		this.gender = gender;
		this.dob = dob;
		this.aadhaarMasked = aadhaarMasked;
		this.panMasked = panMasked;
		this.country = country;
		this.state = state;
		this.district = district;
		this.city = city;
		this.pincode = pincode;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}	

	public Long getPrimaryAccountId() {
		return primaryAccountId;
	}

	public void setPrimaryAccountId(Long primaryAccountId) {
		this.primaryAccountId = primaryAccountId;
	}

	public Long getDetailsId() {
		return detailsId;
	}

	public void setDetailsId(Long detailsId) {
		this.detailsId = detailsId;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public Long getUserId() {
		return user.getId();
	}

	public User getUser() {
		return user;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate date) {
		this.dob = date;
	}

	public String getAadhaarMasked() {
		return aadhaarMasked;
	}

	public void setAadhaarMasked(String aadhaarMasked) {
		this.aadhaarMasked = aadhaarMasked;
	}

	public String getPanMasked() {
		return panMasked;
	}

	public void setPanMasked(String panMasked) {
		this.panMasked = panMasked;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public String getAddressProofFileName() {
		return addressProofFileName;
	}

	public void setAddressProofFileName(String addressProofFileName) {
		this.addressProofFileName = addressProofFileName;
	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	@Override
	public String toString() {
		return "UserDetails [detailsId=" + detailsId + ", user=" + user + ", primaryAccountId=" + primaryAccountId
				+ ", gender=" + gender + ", dob=" + dob + ", aadhaarMasked=" + aadhaarMasked + ", panMasked="
				+ panMasked + ", country=" + country + ", state=" + state + ", district=" + district + ", city=" + city
				+ ", pincode=" + pincode + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", addressProof="
				+ addressProofFileName + ", photo=" + photoFileName + "]";
	}
    
    
}