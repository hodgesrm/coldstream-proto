/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.restapi.jetty;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a specialized error handler to redirect a specific path prefix to
 * /ui. This allows one page apps to refresh from any route. This is basically a
 * form of rewriting. We do it as an error handler because we can't accurately
 * predict in advance which URLs will generate a 404.  Angular application routes
 * overlap the /ui resources, which makes the URL-to-resource mapping ambiguous.
 * Instead, we let through anything that finds a resource and redirect the rest
 * to the /ui path. 
 */
public class UiRedirectHandler extends org.eclipse.jetty.server.handler.ErrorHandler {
	static final Logger logger = LoggerFactory.getLogger(UiRedirectHandler.class);
	private final String name;
	private final String prefix;
	private final String redirectTarget;

	/**
	 * Create a new error handler
	 * 
	 * @param name
	 *            Name of this handler; used in logging messages.
	 * @param path
	 *            Path prefix we want to redirect
	 * @param target
	 *            Target URI to which we redirect
	 */
	public UiRedirectHandler(String name, String path, String target) {
		this.name = name;
		this.prefix = path;
		this.redirectTarget = target;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		logger.info(String.format("Activating error 404 handler: name=%s, path=%s, pathinfo=%s", this.name,
				request.toString(), request.getPathInfo()));
		// If we have a 404 for any prefix match we'll try to redirect to the target.
		// The target must exist or you'll hang with a redirect loop.
		if (response.getStatus() == HttpServletResponse.SC_NOT_FOUND && request.getRequestURI().startsWith(prefix)) {
			logger.info(String.format("Redirecting stray url: uri=%s, target=%s", request.getRequestURI(),
					this.redirectTarget));
			RequestDispatcher dispatcher = request.getRequestDispatcher(this.redirectTarget);
			if (dispatcher != null) {
				response.reset();
				response.sendRedirect(this.redirectTarget);
			} else {
				String msg = "Unable to find redirect dispatcher for URI: " + request.getRequestURI();
				logger.error(msg);
				throw new IOException(msg);
			}
		} else {
			// Pass to super class for default error handling.
			super.handle(target, baseRequest, request, response);
		}
	}

	@Override
	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message,
			boolean showStacks) throws IOException {
		writeErrorPageMessage(request, writer, code, message, request.getRequestURI());
	}

	@Override
	protected void writeErrorPageMessage(HttpServletRequest request, Writer writer, int code, String message,
			String uri) throws IOException {
		String statusMessage = Integer.toString(code) + " " + message;
		logger.error("Problem accessing " + uri + ". " + statusMessage);
		writer.write(statusMessage);
	}
}
