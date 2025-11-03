
package com.fintrust.controller;

import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Datebox;

public class UserSignupController extends SelectorComposer<Component> {

	@Wire
	private Textbox name, email, phone, password, confirmPassword, country, state, city, pincode;
	@Wire
	private Datebox dob;

	@Listen("onClick=#signupBtn")
	public void onSignup() {
		// Basic validations
		if (!isValidEmail(email.getValue())) {
			Messagebox.show("Invalid email address format.", "Error", Messagebox.OK, Messagebox.ERROR);
			return;
		}

		if (!isValidPhone(phone.getValue ())) {
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

		Messagebox.show("Registration successful!", "Success", Messagebox.OK, Messagebox.INFORMATION);
	}

	// ------------------ Validation Methods ------------------

	private boolean isValidEmail(String email) {
		// Basic regex for email format
		return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}

	private boolean isValidPhone(String phone) {
		// Accepts only digits and must be 10 characters
		alert(phone.length() + "");
		return phone != null && phone.matches("\\d{10}");
	}

	private boolean isValidPassword(String password) {
		// Minimum 8 characters
		return password != null && password.length() >= 8;
	}
}
