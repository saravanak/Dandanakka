package com.dandanakka.web.security.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class AuthenticationSuccessHandler extends
		SimpleUrlAuthenticationSuccessHandler {

	@Override
	protected String determineTargetUrl(HttpServletRequest arg0,
			HttpServletResponse arg1) {
		String applicationName = arg0.getParameter("application") ;
		return "/"+applicationName+ super.determineTargetUrl(arg0, arg1);
	}

}
