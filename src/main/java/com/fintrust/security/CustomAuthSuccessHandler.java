package com.fintrust.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

		System.out.println("Successfully login");

		String email = authentication.getName();
		User user = userService.getUserByUserName(email);
		System.out.println(user);
		if (user != null) {

			HttpSession httpSession = request.getSession(true);
//	    Role based access
			if (user.getRole().name().equalsIgnoreCase("ROLE_USER")) {
				// Use HttpSession instead of ZK Session
				httpSession.setAttribute("user_id", user.getId());
				httpSession.setAttribute("user_name", user.getFullName());
				httpSession.setAttribute("user_email", user.getEmail());
				response.sendRedirect(request.getContextPath() + "/user/userDashboard.zul");
			} else {
				httpSession.setAttribute("admin_user_id", user.getId());
				httpSession.setAttribute("user_name", user.getFullName());
				httpSession.setAttribute("admin_user_email", user.getEmail());
				response.sendRedirect(request.getContextPath() + "/admin/adminDashboard.zul");
			}
		}
	}

}
