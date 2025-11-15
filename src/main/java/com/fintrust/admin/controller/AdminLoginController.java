package com.fintrust.admin.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDAOImpl;

public class AdminLoginController extends SelectorComposer<Window>{

	@Wire Textbox email, password;
    private UserDAO userDAO = new UserDAOImpl();

	@Listen("onClick=#submit")
	public void login() {
		String adminName = email.getText();
		String pasw = password.getText();
		
		if(isAuthorize(adminName, pasw)) {
			// Set session for curren user
			Sessions.getCurrent().setAttribute("userName", adminName);
			alert(adminName + ":: " + pasw);
			
			Executions.sendRedirect("/admin/adminDashboard.zul");		
		} else {
			
			alert("Unauthorized User..!");
		}
	}
	
	private boolean isAuthorize(String userName, String password) {
		return (!userName.isBlank()) && userName.equals("admin") && password.equals("123");
	}
}
