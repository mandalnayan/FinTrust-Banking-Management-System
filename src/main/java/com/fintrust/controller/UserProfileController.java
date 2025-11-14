package com.fintrust.controller;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.fintrust.model.Customer;
import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;

public class UserProfileController extends SelectorComposer<Borderlayout> {
	private User user;
	private boolean editMode = false;
	private UserService userService;

	@Override
	public void doAfterCompose(Borderlayout comp) throws Exception {
		super.doAfterCompose(comp);

		// Load user data (e.g., from database or session)
		userService = new UserServiceImpl();
		Customer customer = userService.getLoggedInUser();
		Executions.getCurrent().setAttribute("user", user);
		Messagebox.show("Hii: " + user);
	}
}
