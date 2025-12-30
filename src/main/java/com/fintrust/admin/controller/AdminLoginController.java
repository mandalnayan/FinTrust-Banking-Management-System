package com.fintrust.admin.controller;

import java.sql.Connection;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.UserDAO;
import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.db.DBConnection;

/**
 * Admin login. Will be use in future
 */
public class AdminLoginController extends SelectorComposer<Window>{

	@Wire Textbox email, password;
	
	private UserDAO userDAO;
	@Override
		public void doAfterCompose(Window comp) throws Exception {			
			super.doAfterCompose(comp);
			
			Connection connection = DBConnection.getConnection();
			userDAO = new UserDAOImpl(connection);
		}

	@Listen("onClick=#submit")
	public void login() {
		String adminName = email.getText();
		String pasw = password.getText();
		
		if(isAuthorize(adminName, pasw)) {
			// Set session for current user
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
