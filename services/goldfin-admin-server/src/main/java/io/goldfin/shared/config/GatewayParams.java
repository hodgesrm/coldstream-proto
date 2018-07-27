/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.config;

/**
 * Server configuration parameters.
 */
public class GatewayParams {
	private int securePort = 8443;
	private String keyStorePath = "conf/keystore.jks";
	private String keyStorePassword = "secret";
	private String documentBucket;

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

	public String getDocumentBucket() {
		return documentBucket;
	}

	public void setDocumentBucket(String documentBucket) {
		this.documentBucket = documentBucket;
	}
}