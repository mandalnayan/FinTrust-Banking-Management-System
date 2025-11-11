package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.UserDAOImpl;

public class UserLoginController extends SelectorComposer<Window>{

	@Wire Textbox email, password;
    private UserDAO userDAO = new UserDAOImpl();

	@Listen("onClick=#submit")
	public void login() {
		String userName = email.getText();
		String pasw = password.getText();
		
		if(userDAO.isAuthorize(userName, pasw)) {
			// Set session for curren user
			Sessions.getCurrent().setAttribute("currentUser", userName);
			
			Executions.sendRedirect("/user/userDashboard.zul");		
		} else {
			alert("Unauthorized User..!");
		}
	}
}
