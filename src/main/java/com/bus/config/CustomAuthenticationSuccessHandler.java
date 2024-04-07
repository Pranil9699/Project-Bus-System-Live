package com.bus.config;

import com.bus.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user.getRole().equals("ROLE_ADMIN")) {
            response.sendRedirect("/admin/index");
        } else if (user.getRole().equals("ROLE_USER")) {
            response.sendRedirect("/user/index");
        } else {
            request.getSession().setAttribute("error", "Unknown role");
            response.sendRedirect("/signin");
        }
    }

}