/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.config.DataSeriesParams;

/**
 * Handles operations related to batch analysis of data series.
 */
public class DataSeriesAnalysisManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(DataSeriesAnalysisManager.class);
	private ManagementContext context;

	// Names of our queues.
	private String dataSeriesRequestQueue;
	private String dataSeriesResponseQueue;

	// Thread pool to read data series responses.
	private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	/**
	 * Ensure that queues used for OCR exist.
	 */
	@Override
	public void prepare() {
		// Ensure the queues exist.
		DataSeriesParams config = context.getDataSeriesParams();
		dataSeriesRequestQueue = ensureQueue(config.getRequestQueue());
		dataSeriesResponseQueue = ensureQueue(config.getResponseQueue());

		// Start the response queue thread.
		DataSeriesResponseQueueTask task = new DataSeriesResponseQueueTask(context, dataSeriesResponseQueue);
		threadPool.submit(task);
	}

	private String ensureQueue(String queueHandle) {
		// Ensure the queue exists.
		QueueConnection queue = CloudConnectionFactory.getInstance().getQueueConnection(queueHandle);
		if (queue.queueExists()) {
			logger.info(String.format("Data series queue exists: %s", queueHandle));
		} else {
			logger.info(String.format("Data series queue does not exist, creating: %s", queueHandle));
			queue.queueCreate(false);
		}
		return queueHandle;
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	/**
	 * Submit a data series for analysis.
	 * 
	 * @param tenantId
	 *            Tenant ID
	 * @param dataSeries
	 *            DataSeries record
	 */
	public void process(String tenantId, DataSeries dataSeries) {
		// Generate a request to scan the dataSeries.
		QueueConnection conn = CloudConnectionFactory.getInstance().getQueueConnection(dataSeriesRequestQueue);
		logger.info(String.format("Requesting data series analysis: tenantId=%s, dataSeriesId=%s, name=%s", tenantId,
				dataSeries.getId().toString(), dataSeries.getName()));
		StructuredMessage request = new StructuredMessage().setOperation("process").setType("request")
				.setXactTag(dataSeries.getId().toString()).setTenantId(tenantId).encodeContent(dataSeries);
		conn.send(request);
		logger.info(String.format("Data series analysis request enqueued: tenantId=%s, dataSeriesId=%s, name=%s",
				tenantId, dataSeries.getId().toString(), dataSeries.getName()));
	}
}