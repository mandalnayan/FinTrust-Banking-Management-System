package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.UserDAOImpl;

public class PasswordChangeController extends SelectorComposer<Window>{

	@Wire Textbox password, conformPassword;
	
	@Wire Checkbox show;
	
	@Listen("onClick=#submit")
	public void submitPassword() {
		if (password.getValue().isBlank() || !isPasswordMatched()) {
				Messagebox.show(password.getValue().isBlank() ? "Password can't be empty" : "Password didn't matched..!");
		}
		if (new UserDAOImpl().updatePassword(password.getValue())) {
				Clients.showNotification("Password changed!", "info", null, "top_center", 3000);
				Sessions.getCurrent().removeAttribute("currentUser");
				
				Executions.sendRedirect("/user/login.zul");
		}
		else 
			Clients.showNotification("Failed to change. Please try again!", "error", null, "top_center", 3000);
	}
	
	@Listen("onCheck = #show")
	public void togglePassword() {
	    if (show.isChecked()) {
	        password.setType("text");
	        conformPassword.setType("text");
	    } else {
	        password.setType("password");
	        conformPassword.setType("password");
	    }
	}
	
	private boolean isPasswordMatched() {
		return password.getValue().trim().equals(conformPassword.getValue().trim());
	}
}
