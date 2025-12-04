package com.fintrust.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.fintrust.model.User;
import com.fintrust.service.UserService;
import com.fintrust.util.NotificationUtil;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;

	public CustomAuthSuccessHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String email = authentication.getName();
		User user = userService.getUserByUserName(email);
		if (user != null) {
			Session session = Sessions.getCurrent();
			session.setAttribute("user_id", user.getId());
			session.setAttribute("user_name", user.getFullName());
			NotificationUtil.push("info", "Welocme back " + user.getFullName());
		}

		// Redirect to dashboard or home page
		response.sendRedirect(request.getContextPath() + "/user/userDashboard.zul");
	}
}
