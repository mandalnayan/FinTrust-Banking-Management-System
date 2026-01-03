package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.service.UserServiceImpl;

public class ResetPasswordController extends SelectorComposer<Div>{

	@Wire Textbox password, confirmPassword;
	
	@Wire Checkbox show;
	
	@Wire Label l_status_mes;
	
	@Listen("onClick=#resetBtn")
	public void submitPassword() {
		if (password.getValue().isBlank() || !isPasswordMatched()) {
				String statusMessage = password.getValue().isBlank() ? "Password can't be empty" : "Password didn't matched..!";
				 Sessions.getCurrent().setAttribute("statusMessage", statusMessage);	
				 l_status_mes.setStyle("color: red");
		}
		else if (new UserServiceImpl().updatePassword(password.getValue())) {
				Clients.showNotification("Password changed!", "info", null, "top_center", 3000);
				Sessions.getCurrent().removeAttribute("userEmail");
				Sessions.getCurrent().invalidate();
				
				Executions.sendRedirect("/user/login.zul");
		}
		else 
			Clients.showNotification("Failed to change. Please try again!", "error", null, "top_center", 3000);
	}
	
	@Listen("onClick = #togglePwd")
	public void togglePassword() {
	   Textbox textbox = (Textbox) getSelf().getFellow("password");
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
	
	@Listen("onClick = #togglePwd_confPwd")
	public void toggleConfirmPassword() {
	   Textbox textbox = (Textbox) getSelf().getFellow("confirmPassword");
	   Button toggleBtn = (Button) getSelf().getFellow("togglePwd_confPwd");
	   
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
		return password.getValue().trim().equals(confirmPassword.getValue().trim());
	}
}
