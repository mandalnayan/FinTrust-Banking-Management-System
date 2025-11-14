package com.fintrust.model;

import java.sql.Date;

public class Customer {

    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String country;
    private String state;
    private String dist;
    private String city;
    private String pincode;
    private Date dob;
    private String password;
    private Date registeredDate;
    private String status;
    private String image;
    private Boolean twoFactor;

    public Customer() {}
   
    public Customer(Long customerId, String name, String email, String phone, String gender, String country,
            String state, String dist, String city, String pincode, String image,
            java.sql.Date registeredDate, java.sql.Date dob) {
    
    this.customerId = customerId;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.gender = gender;
    this.country = country;
    this.state = state;
    this.dist = dist;
    this.city = city;
    this.pincode = pincode;
    this.image = image;

    // Convert SQL Date to String (because POJO stores String)
    this.registeredDate = (registeredDate != null) ? registeredDate : null;
    this.dob = (dob != null) ? dob : null;
}

    // ----------- Getters & Setters --------------

	public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getTwoFactor() {
        return twoFactor;
    }

    public void setTwoFactor(Boolean twoFactor) {
        this.twoFactor = twoFactor;
    }
}

