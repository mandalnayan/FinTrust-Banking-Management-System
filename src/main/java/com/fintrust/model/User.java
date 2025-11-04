package com.fintrust.model;

import java.sql.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String password;
    private String country;
    private String state;
    private String dist;
    private String city;
    private String pincode;
    private Date dob;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getDist() { return dist; }
    public void setDist(String dist) { this.dist = dist; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }
}
