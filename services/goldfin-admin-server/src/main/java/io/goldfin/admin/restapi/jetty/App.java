/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.restapi.jetty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.managers.DataSeriesAnalysisManager;
import io.goldfin.admin.managers.DataSeriesManager;
import io.goldfin.admin.managers.DocumentManager;
import io.goldfin.admin.managers.InvoiceManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.managers.OcrManager;
import io.goldfin.admin.managers.TenantManager;
import io.goldfin.admin.managers.UserManager;
import io.goldfin.admin.managers.VendorManager;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.utilities.YamlHelper;

public class App {
	static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		logger.info("Starting application");
		// Load server configuration.
		ServerConfig serverConfig = loadServerConfig();

		// Instantiate the server and set up HTTPS connector.
		Server jettyServer = new Server();
		setupHttpsConnector(jettyServer, serverConfig);

		// Create handlers.
		ServletContextHandler servletHandler = configureServletHandler();
		ConstraintSecurityHandler securityHandler = configureSecurityHandler();
		ContextHandler resourceContextHandler = configureResourceHandler();

		// Add security handler to server and chain servlet handler to it.
		securityHandler.setHandler(servletHandler);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceContextHandler, securityHandler });
		jettyServer.setHandler(handlers);

		// Initialize managers.
		try {
			setupManagers();
		} catch (Exception e) {
			logger.error("Fatal initialization error", e);
		}

		// Start web server.
		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}
	}

	/**
	 * Load the server configuration file.
	 */
	private static ServerConfig loadServerConfig() {
		File configFile = new File("conf/server-config.yaml");
		try {
			ServerConfig config = YamlHelper.readFromFile(configFile, ServerConfig.class);
			return config;
		} catch (IOException e) {
			throw new RuntimeException("Unable to read config file: " + configFile.getAbsolutePath(), e);
		}
	}

	/**
	 * Set up a single HTTPS listener. For now HTTP is not supported at all.
	 * 
	 * @see <a href=
	 *      "https://www.eclipse.org/jetty/documentation/9.4.x/embedded-examples.html">Jetty
	 *      Embedded Server documentation</a>
	 */
	private static void setupHttpsConnector(Server server, ServerConfig serverConfig) {
		// Set up an SSL context. TODO: Define allowed/excluded protocols and cipher
		// suites.
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(new File(serverConfig.getKeyStorePath()).getAbsolutePath());
		sslContextFactory.setKeyStorePassword(serverConfig.getKeyStorePassword());
		// sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

		// Define the HTTPS configuration. The SecureRequestCustomizer handles
		// authentication.
		HttpConfiguration https_config = new HttpConfiguration();
		https_config.setSecureScheme("https");
		https_config.setSecurePort(serverConfig.getSecurePort());
		https_config.setOutputBufferSize(32768);
		SecureRequestCustomizer src = new SecureRequestCustomizer();
		src.setStsMaxAge(2000);
		src.setStsIncludeSubDomains(true);
		https_config.addCustomizer(src);

		// Now create the connector and configure the port and timeout.
		// We create a second ServerConnector, passing in the http configuration
		// we just made along with the previously created ssl context factory.
		// Next we set the port and a longer idle timeout.
		ServerConnector https = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(https_config));
		https.setPort(serverConfig.getSecurePort());
		https.setIdleTimeout(500000);

		// Assign the connector the server.
		server.setConnectors(new Connector[] { https });
	}

	/** Configure application logic managers. */
	private static void setupManagers() throws IOException {
		File dbmsYaml = new File("conf/dbms.yaml");
		ConnectionParams serviceConnectionParams = YamlHelper.readFromFile(dbmsYaml, ConnectionParams.class);
		logger.info("Reading DBMS connections: " + dbmsYaml.getAbsolutePath());

		// Initialize cloud services.
		File awsYaml = new File("conf/aws.yaml");
		CloudConnectionFactory cloudFactory = CloudConnectionFactory.getInstance();
		cloudFactory.setConnectionParamsFile(awsYaml);

		// Configure managers in registry.
		ManagerRegistry registry = ManagerRegistry.getInstance();
		registry.initialize(serviceConnectionParams, awsYaml);
		// Managers for API services.
		registry.addManager(new UserManager());
		registry.addManager(new TenantManager());
		registry.addManager(new DataSeriesManager());
		registry.addManager(new InvoiceManager());
		registry.addManager(new DocumentManager());
		registry.addManager(new VendorManager());
		// Managers to mediate with backend pipelines.
		registry.addManager(new OcrManager());
		registry.addManager(new DataSeriesAnalysisManager());
		registry.start();
	}

	/** Create a security constraint handler to handle request authorization. */
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
		String[] paths = { "login", "tenant", "user" };
		List<ConstraintMapping> mappings = new ArrayList<ConstraintMapping>();
		for (String path : paths) {
			ConstraintMapping mapping = new ConstraintMapping();
			mapping.setPathSpec("/api/v1/" + path);
			mapping.setConstraint(constraint);
			mappings.add(mapping);
		}
		security.setConstraintMappings(mappings);

		// Set the authenticator class, which checks for presence of a valid
		// header key. For now there's no login service.
		security.setAuthenticator(new SecurityAuthenticator());
		security.setLoginService(null);

		return security;
	}

	/**
	 * Create context handler for servlets. Important: this needs to have the
	 * correct package names for the API implementation classes or you will get 404s
	 * on all requests.
	 */
	private static ServletContextHandler configureServletHandler() {
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");

		ServletHolder jerseyServlet = servletHandler.addServlet(ServletContainer.class, "/api/v1/*");
		jerseyServlet.setInitOrder(0);

		// Find packages that have controllers.
		jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
				"io.goldfin.admin.service.api.service");

		// Register provider for multi-part requests.
		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", MultiPartFeature.class.getName());

		// Add CORS filter, and then use the provided FilterHolder to configure it. 
		// (Original example from StackOverflow:
		// https://stackoverflow.com/questions/28190198/cross-origin-filter-with-embedded-jetty)
		FilterHolder cors = servletHandler.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
		cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD,UPDATE,DELETE");
		// For now allow all headers on requests. 
		String allowedHeaders = "*";
		cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, allowedHeaders);
		// Allow API key header to be exposed along with content-length.  
		String exposedResponseHeaders = String.format("Content-Length,%s", SecurityAuthenticator.API_KEY_HEADER);
		cors.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, exposedResponseHeaders);

		return servletHandler;
	}

	/** Create a resource handler to serve up files. */
	private static ContextHandler configureResourceHandler() {
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setResourceBase("content");

		ContextHandler contextHandler = new ContextHandler();
		contextHandler.setContextPath("/content");
		contextHandler.setHandler(resourceHandler);
		return contextHandler;
	}
}