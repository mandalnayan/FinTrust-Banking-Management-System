
package com.fintrust.controller;

import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.SelectorComposer;

import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
	private Textbox name, email, phoneNumber, password, confirmPassword;
	
    private UserService userService = new UserServiceImpl();

	@Listen("onClick=#signup")
	public void onSignup() {		
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
        user.setFullName(name.getValue());
        user.setEmail(email.getValue());
        user.setPhone(phoneNumber.getValue());
        user.setRole(User.Role.ROLE_USER);
        user.setPassword(password.getValue());
        

        // Send data to service layer
        boolean success = userService.registerUser(user);

        if (success) {
            Messagebox.show("Signup successful! You can now log in.", "Success", Messagebox.OK, Messagebox.INFORMATION);
            Executions.sendRedirect("/user/login.zul");
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
	    if (phone == null) return false;

	    // Regex Explanation:
	    // ^(\+91[\-\s]?)?  → optional +91 or +91- or +91(space)
	    // 0?               → optional leading 0
	    // [6-9]\d{9}$      → starts with 6-9 and followed by 9 digits
	    return phone.matches("^(\\+91[\\-\\s]?)?0?[6-9]\\d{9}$");
	}

	private boolean isValidPassword(String password) {
		// Minimum 8 characters
		return password != null && password.length() >= 1;
	}
}
