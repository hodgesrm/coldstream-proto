/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.data;

/**
 * JDBC connection parameters.
 */
public class DbmsParams {
	private String driver = "org.postgresql.Driver";
	private String url;
	private String user;
	private String password;
	private String adminSchema = "admin";

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAdminSchema() {
		return adminSchema;
	}

	public void setAdminSchema(String adminSchema) {
		this.adminSchema = adminSchema;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getSimpleName());
		buf.append(": driver=").append(driver);
		buf.append(", url=").append(url);
		buf.append(", user=").append(user);
		buf.append(", adminSchema=").append(adminSchema);
		return buf.toString();
	}
}
