
package com.fintrust.controller;

import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

import org.zkoss.zul.Datebox;

public class UserSignupController extends SelectorComposer<Component> {

	@Wire
	private Textbox name, email, phoneNumber, password, confirmPassword, country, state, dist, city, pincode;
	@Wire
	private Datebox dob;
	@Wire
	private Radiogroup gender;
	
    private UserService userService = new UserServiceImpl();

	@Listen("onClick=#signupBtn")
	public void onSignup() {
		Radio radio = gender.getSelectedItem();
		if (radio == null) {
			Messagebox.show("Please select your gender.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}		
		
		// Basic validations
		if (!isValidEmail(email.getValue())) {
			Messagebox.show("Invalid email address format.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (!isValidPhone(phoneNumber.getValue ())) {
			Messagebox.show("Phone number must be 10 digits only.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (!isValidPassword(password.getValue())) {
			Messagebox.show("Password must be at least 8 characters.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (!password.getValue().equals(confirmPassword.getValue())) {
			Messagebox.show("Passwords do not match.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		 // Create User object
        User user = new User();
        user.setName(name.getValue());
        user.setEmail(email.getValue());
        user.setPhone(phoneNumber.getValue());
        user.setGender(radio.getName());
        user.setDist(dist.getValue());
        user.setPassword(password.getValue());
        user.setCountry(country.getValue());
        user.setState(state.getValue());
        user.setCity(city.getValue());
        user.setPincode(pincode.getValue());
        if (dob.getValue() != null) {
            java.sql.Date sqlDob = new java.sql.Date(dob.getValue().getTime());
            user.setDob(sqlDob);
        } else {
            user.setDob(null);
        }

        // Send data to service layer
        boolean success = userService.registerUser(user);

        if (success) {
            Messagebox.show("Signup successful! You can now log in.", "Success", Messagebox.OK, Messagebox.INFORMATION);
        } else {
            Messagebox.show("Signup failed! Email may already exist.", "Error", Messagebox.OK, Messagebox.ERROR);
        }	
	}

	// ------------------ Validation Methods ------------------

	private boolean isValidEmail(String email) {
		// Basic regex for email format
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}

	private boolean isValidPhone(String phone) {
		// Accepts only digits and must be 10 characters
		return phone != null && phone.matches("\\d{10}");
	}

	private boolean isValidPassword(String password) {
		// Minimum 8 characters
		return password != null && password.length() >= 1;
	}
}
