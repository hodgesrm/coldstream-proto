/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.util.Properties;

/**
 * Cloud connection parameters.
 */
public class AwsConnectionParams {
	private String accessKeyId;
	private String secretAccessKey;
	private Properties s3 = new Properties();
	private Properties ocr = new Properties();
	private Properties dataSeries = new Properties();

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

	public Properties getS3() {
		return s3;
	}

	public void setS3(Properties s3) {
		this.s3 = s3;
	}

	public Properties getOcr() {
		return ocr;
	}

	public void setOcr(Properties ocr) {
		this.ocr = ocr;
	}

	public Properties getDataSeries() {
		return dataSeries;
	}

	public void setDataSeries(Properties dataSeries) {
		this.dataSeries = dataSeries;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getSimpleName());
		buf.append(" s3=").append(s3.toString());
		buf.append(", sqs=").append(ocr.toString());
		return buf.toString();
	}
}