package com.fintrust.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fintrust.dao.impl.UserDAOImpl;
import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.service.UserServiceImpl;
import com.fintrust.util.NotificationUtil;

public class ResetPasswordController extends SelectorComposer<Component> {

	@Wire
	Textbox oldPassword, password, confirmPassword;

	@Wire
	Checkbox show;

	@Wire
	Label l_status_mes;

	@Listen("onClick=#resetPswBtn")
	public void submitPassword() {
		if (password.getValue().isBlank() || !isPasswordMatched()) {
			String statusMessage = password.getValue().isBlank() ? "Password can't be empty"
					: "Password didn't matched..!";
			Sessions.getCurrent().setAttribute("statusMessage", statusMessage);
			l_status_mes.setStyle("color: red");
		} else if (new UserServiceImpl().updatePassword(password.getValue())) {
			Clients.showNotification("Password changed!", "info", null, "top_center", 3000);
			Sessions.getCurrent().removeAttribute("userEmail");
			Sessions.getCurrent().invalidate();

			Executions.sendRedirect("/user/login.zul");
		} else
			Clients.showNotification("Failed to change. Please try again!", "error", null, "top_center", 3000);
	}

	@Listen("onClick=#changePswBtn")
	public void changePassword() {
		UserService userService = new UserServiceImpl();
		String encodedPassword = userService.getLoggedInUserPassword();

		boolean isPasswordEmpty = (oldPassword.getValue().isBlank() || confirmPassword.getValue().isBlank()
				|| password.getValue().isBlank());
		if (isPasswordEmpty || !isPasswordMatched()) {
			String statusMessage = isPasswordEmpty ? "Password can't be empty" : "Password didn't matched..!";
			NotificationUtil.showInstant("error", statusMessage);
			Sessions.getCurrent().setAttribute("statusMessage", statusMessage);
			l_status_mes.setStyle("color: red");
		} else if(oldPassword.getValue().equals(password.getValue())) {
			NotificationUtil.showInstant("warning", "New password must be different from current password");
		}
		else if (!userService.isPasswordMatch(oldPassword.getValue(), encodedPassword)) {
			String statusMessage = "Password didn't matched with old password. Please enter correct password";
			NotificationUtil.showInstant("warning", "Password didn't matched with old password");
		} else if (new UserServiceImpl().updatePassword(password.getValue())) {

			NotificationUtil.push("info", "Password changed!");
			Executions.sendRedirect("");		
		} else {
			NotificationUtil.showInstant("info", "Failed to change. Please try again!");

		}
	}

	@Listen("onClick = #toggleOldPwd")
	public void toggleOldPassword() {
		Textbox textbox = (Textbox) getSelf().getFellow("oldPassword");
		Button toggleBtn = (Button) getSelf().getFellow("toggleOldPwd");

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
