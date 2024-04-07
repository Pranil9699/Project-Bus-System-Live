package com.bus.config;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String errorMessage = "Unknown error occurred";
		if (exception instanceof LockedException) {
			errorMessage = "Your account is locked. Please contact the administrator.";
		} else if (exception instanceof DisabledException) {
			errorMessage = "Your account is disabled. Please contact the administrator.";
		} else if (exception instanceof AccountExpiredException) {
			errorMessage = "Your account has expired. Please contact the administrator.";
		} else if (exception instanceof CredentialsExpiredException) {
			errorMessage = "Your credentials have expired. Please login again.";
		} else if (exception instanceof BadCredentialsException) {
			errorMessage = "Invalid username or password. Please try again.";
		}

		request.getSession().setAttribute("error", errorMessage);

		// Redirect to login page without exposing error message in URL
		getRedirectStrategy().sendRedirect(request, response, "/signin");
	}
}