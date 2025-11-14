package com.fintrust.model;

import java.sql.Date;

public class User {
    private long id;
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
    private Date registeredDate;
    private String image;
	private boolean twoFactor;
  
	private String password;

	public User() {}
    
      public User(long id, String name, String email, String phone, String gender, String country, String state,
			String dist, String city, String pincode, String image, Date registeredDate, Date dob) {
		super();
		this.id = id;
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
		this.registeredDate = registeredDate;
		this.dob = dob;
	}
      
	public String getImage() {
		return image;
	}

	  public void setImage(String image) {
		  this.image = image;
	  }

	// Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

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
    
    public boolean isTwoFactor() {
		return twoFactor;
	}
    
    public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}
    
	public void setTwoFactor(boolean twoFactor) {
		this.twoFactor = twoFactor;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + ", gender=" + gender
				+ ", country=" + country + ", state=" + state + ", dist=" + dist + ", city=" + city + ", pincode="
				+ pincode + ", dob=" + dob + ", twoFactor=" + twoFactor + "]";
	}
	
	
}
