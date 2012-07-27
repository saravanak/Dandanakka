package com.dandanakka.web.security.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

public class LoginUrlAuthenticationEntryPoint
		extends
		org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint {

	@Override
	protected String determineUrlToUseForThisRequest(
			HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) {
		String loginUrl = super.determineUrlToUseForThisRequest(request,
				response, exception);
		String applicationName = request.getRequestURI().split("/")[2];

		return "/" + applicationName + loginUrl;
	}

}
