//  ========================================================================
//  Copyright (c) 2017 Goldfin.io.  All rights reserved.
//  ========================================================================
package io.goldfin.invoice.restapi.jetty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		logger.info("Starting application");
		Server jettyServer = new Server(8080);
		
		// Create handlers. 
		ServletContextHandler servletHandler = configureServletHandler();
		ConstraintSecurityHandler securityHandler = configureSecurityHandler();

		// Add security handler to server and chain servlet handler to it. 
		jettyServer.setHandler(securityHandler);
		securityHandler.setHandler(servletHandler);
		
		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}
	}
	
	private static ConstraintSecurityHandler configureSecurityHandler() {
		// Create a security handler. 
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();

        // This constraint requires authentication and in addition that an
        // authenticated user be a member of a given set of roles for
        // authorization purposes.
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { "user", "admin" });

        // Create mappings for each REST URL with the constraint and add to 
        // the security handler. 
        String[] paths = {"export", "host", "invoice", "tenant", "user"};
        List<ConstraintMapping> mappings = new ArrayList<ConstraintMapping>();
        for (String path: paths) {
            ConstraintMapping mapping = new ConstraintMapping();
            mapping.setPathSpec("/api/v1/" + path);
            mapping.setConstraint(constraint);
            mappings.add(mapping);
        }
        security.setConstraintMappings(mappings);
        
        // Set the authenticator class, which checks for presence of a valid
        // header key.  For now there's no login service. 
        security.setAuthenticator(new SecurityAuthenticator());
        security.setLoginService(null);
        
        return security;
	}

	private static ServletContextHandler configureServletHandler() {
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");
		
		ServletHolder jerseyServlet = servletHandler.addServlet(ServletContainer.class, "/api/v1/*");
		jerseyServlet.setInitOrder(0);

		// Find packages that have controllers.
		jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
				"io.goldfin.front.invoice.api.service");

		// Register provider for multi-part requests. 
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", MultiPartFeature.class.getName());

		return servletHandler;
	}
}
