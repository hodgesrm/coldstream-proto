/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

/**
 * Amazon cloud connection parameters.
 */
public class AwsParams {
	private String group;
	private String accessKeyId;
	private String secretAccessKey;
	private String region;
	private String s3Root;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getS3Root() {
		return s3Root;
	}

	public void setS3Root(String s3Root) {
		this.s3Root = s3Root;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.getClass().getSimpleName());
		buf.append(" group=").append(group);
		buf.append(", secretAccessKey=").append(secretAccessKey);
		buf.append(", region=").append(region);
		buf.append(", s3Root=").append(s3Root);
		return buf.toString();
	}
}