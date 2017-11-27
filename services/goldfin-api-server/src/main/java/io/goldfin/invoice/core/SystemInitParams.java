/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

/**
 * Parameters to initialize a new invoice service.
 */
public class SystemInitParams {
	private String driver = "org.postgresql.Driver";
	private String url;
	private String adminUser;
	private String adminPassword;
	private String serviceDb;
	private String serviceUser;
	private String servicePassword;

	/**
	 * @return the driver
	 */
	public String getDriver() {
		return driver;
	}

	/**
	 * @param driver
	 *            the driver to set
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the adminUser
	 */
	public String getAdminUser() {
		return adminUser;
	}

	/**
	 * @param adminUser
	 *            the adminUser to set
	 */
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	/**
	 * @return the adminPassword
	 */
	public String getAdminPassword() {
		return adminPassword;
	}

	/**
	 * @param adminPassword
	 *            the adminPassword to set
	 */
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	/**
	 * @return the serviceDb
	 */
	public String getServiceDb() {
		return serviceDb;
	}

	/**
	 * @param serviceDb
	 *            the serviceDb to set
	 */
	public void setServiceDb(String serviceDb) {
		this.serviceDb = serviceDb;
	}

	/**
	 * @return the serviceUser
	 */
	public String getServiceUser() {
		return serviceUser;
	}

	/**
	 * @param serviceUser
	 *            the serviceUser to set
	 */
	public void setServiceUser(String serviceUser) {
		this.serviceUser = serviceUser;
	}

	/**
	 * @return the servicePassword
	 */
	public String getServicePassword() {
		return servicePassword;
	}

	/**
	 * @param servicePassword
	 *            the servicePassword to set
	 */
	public void setServicePassword(String servicePassword) {
		this.servicePassword = servicePassword;
	}

}