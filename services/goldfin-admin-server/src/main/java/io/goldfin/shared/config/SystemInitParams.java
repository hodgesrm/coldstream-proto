/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.config;

/**
 * Parameters to initialize a new invoice service.
 */

public class SystemInitParams {
	// DB connection and root account. 
	private String driver = "org.postgresql.Driver";
	private String url;
	private String adminUser;
	private String adminPassword;
	
	// DB that contains tenant data. 
	private String serviceDb;
	private String serviceUser;
	private String servicePassword;
	private String serviceSchema = "admin";
	
	// Application sysadmin user
	private String sysUser;
	private String sysPassword;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getServiceDb() {
		return serviceDb;
	}

	public void setServiceDb(String serviceDb) {
		this.serviceDb = serviceDb;
	}

	public String getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(String serviceUser) {
		this.serviceUser = serviceUser;
	}

	public String getServicePassword() {
		return servicePassword;
	}

	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}

	public String getServiceSchema() {
		return serviceSchema;
	}

	public void setServiceSchema(String serviceSchema) {
		this.serviceSchema = serviceSchema;
	}

	public String getSysUser() {
		return sysUser;
	}

	public void setSysUser(String sysUser) {
		this.sysUser = sysUser;
	}

	public String getSysPassword() {
		return sysPassword;
	}

	public void setSysPassword(String sysPassword) {
		this.sysPassword = sysPassword;
	}
}