/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.config;

import io.goldfin.shared.cloud.AwsParams;
import io.goldfin.shared.data.DbmsParams;

/**
 * Service configuration consisting of individual service configuration
 * parameter classes.
 */
public class ServiceConfig {
	private AwsParams aws;
	private DbmsParams dbms;
	private GatewayParams gateway;
	private OcrParams ocr;
	private DataSeriesParams dataSeries;

	public AwsParams getAws() {
		return aws;
	}

	public void setAws(AwsParams aws) {
		this.aws = aws;
	}

	public DbmsParams getDbms() {
		return dbms;
	}

	public void setDbms(DbmsParams dbms) {
		this.dbms = dbms;
	}

	public GatewayParams getGateway() {
		return gateway;
	}

	public void setGateway(GatewayParams gateway) {
		this.gateway = gateway;
	}

	public OcrParams getOcr() {
		return ocr;
	}

	public void setOcr(OcrParams ocr) {
		this.ocr = ocr;
	}

	public DataSeriesParams getDataSeries() {
		return dataSeries;
	}

	public void setDataSeries(DataSeriesParams dataSeries) {
		this.dataSeries = dataSeries;
	}
}