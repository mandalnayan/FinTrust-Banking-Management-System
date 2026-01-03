package com.fintrust.security;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fintrust.util.NotificationUtil;

import javax.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import java.io.IOException;

public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        System.out.println("Login failed for user: " + request.getParameter("username"));
     //   NotificationUtil.push("error", "Login failed! Invalid credentials. " + exception.getMessage());
        exception.printStackTrace();
        response.sendRedirect(request.getContextPath() + "/user/login.zul?error=true");
    }
}
