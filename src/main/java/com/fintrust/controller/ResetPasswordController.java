package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import zcom.finrust.dao_copy.UserDAOImpl;

public class ResetPasswordController extends SelectorComposer<Window>{

	@Wire Textbox password, conformPassword;
	
	@Wire Checkbox show;
	
	@Listen("onClick=#resetBtn")
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
	
	@Listen("onClick = #togglePwd")
	public void togglePassword() {
	   Textbox textbox = (Textbox) getSelf().getFellow("newPassword");
	   Button toggleBtn = (Button) getSelf().getFellow("togglePwd");
	   
	   if (textbox.getType().equals("password")) {
		   textbox.setType("text");
		   toggleBtn.setIconSclass("z-icon-eye-slash");
		   toggleBtn.setTooltiptext("Hide Password");
	   } else {
		   textbox.setType("password");
		   toggleBtn.setIconSclass("z-icon-eye");
		   toggleBtn.setTooltiptext("Show Password");
	   }
	}
	
	private boolean isPasswordMatched() {
		return password.getValue().trim().equals(conformPassword.getValue().trim());
	}
}
