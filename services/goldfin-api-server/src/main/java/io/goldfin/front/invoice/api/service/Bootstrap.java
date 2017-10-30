package io.goldfin.front.invoice.api.service;

import io.goldfin.invoice.restapi.jetty.App;
import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.*;

import io.swagger.models.auth.*;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class Bootstrap extends HttpServlet {
	static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	
  @Override
  public void init(ServletConfig config) throws ServletException {
	  logger.info("Starting init method!");
    Info info = new Info()
      .title("Swagger Server")
      .description("Goldfin Invoice Analysis")
      .termsOfService("http://swagger.io/terms/")
      .contact(new Contact()
        .email("rhodges@skylineresearch.comm"))
      .license(new License()
        .name("")
        .url("http://unlicense.org"));

    ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);

    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
  }
}
