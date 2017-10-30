//  ========================================================================
//  Copyright (c) 2017 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.invoice.restapi.jetty;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks requests to ensure they are authorized. 
 */
public class SecurityFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	public void destroy() {
		logger.info("Destroying filter: " + this.getClass().getSimpleName());
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		logger.info("Filtering request");
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			logger.info(String.format("%s: %s", name, request.getAttribute(name)));
		}
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String name = names.nextElement();
			logger.info(String.format("%s", name));
		}
		
		HttpServletRequest r = (HttpServletRequest) request;
		logger.info("Path Info: " + r.getPathInfo());
		logger.info("Context Path: " + r.getContextPath());
		logger.info("Servlet path: " + r.getServletPath());
		logger.info("Header: " + r.getHeader("vnd-io-goldfin-auth"));
	}

	public void init(FilterConfig arg0) throws ServletException {
		logger.info("Initializing filter: " + this.getClass().getSimpleName());
	}

}
