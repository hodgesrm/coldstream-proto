/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.restapi.jetty;

/**
 * Server configuration parameters.
 */
public class ServerConfig {
	private int securePort = 8443;
	private String keyStorePath = "conf/keystore.jks";
	private String keyStorePassword = "secret";

	public int getSecurePort() {
		return securePort;
	}

	public void setSecurePort(int securePort) {
		this.securePort = securePort;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
}