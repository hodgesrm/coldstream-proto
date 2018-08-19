/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.tenant.DataSeriesDataService;
import io.goldfin.admin.data.tenant.HostDataService;
import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.DataSeries.StateEnum;
import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.model.Result;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.QueueConnection;
import io.goldfin.shared.cloud.StructuredMessage;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.utilities.JsonHelper;

/**
 * Executes an endless loop to look for and process data series analysis
 * responses.
 */
public class DataSeriesResponseQueueTask implements Runnable {
	static final Logger logger = LoggerFactory.getLogger(DataSeriesResponseQueueTask.class);

	private final ManagementContext context;
	private final String dataSeriesResponseQueue;

	public DataSeriesResponseQueueTask(ManagementContext context, String dataSeriesResponseQueue) {
		this.context = context;
		this.dataSeriesResponseQueue = dataSeriesResponseQueue;
	}

	/**
	 * Read from queue indefinitely.
	 */
	@Override
	public void run() {
		try {
			// Open up a queue connection.
			logger.info("Opening connection to response queue");
			QueueConnection queue = CloudConnectionFactory.getInstance().getQueueConnection(dataSeriesResponseQueue);

			// Read from the queue until we are interrupted.
			long livenessMillis = System.currentTimeMillis() + 60000;
			long successful = 0;
			long errors = 0;
			while (!Thread.currentThread().isInterrupted()) {
				// Periodically show we're alive.
				if (System.currentTimeMillis() > livenessMillis) {
					logger.info(String.format("Data series response handler liveness check: successful=%d, errors=%d",
							successful, errors));
					livenessMillis = System.currentTimeMillis() + 60000;
				}
				// Try to read a message.
				try {
					if (this.readFromQueue(queue)) {
						successful++;
					}
				} catch (Exception e) {
					logger.error("Queue read operation failed", e);
					errors++;
				}
			}
		} catch (Throwable t) {
			logger.error("Response handler failed", t);
		}
	}

	/**
	 * Read and process message from queue.
	 * 
	 * @param queue
	 *            SQS queue
	 * @return True if a message was read, otherwise false
	 */
	private boolean readFromQueue(QueueConnection queue) {
		// Look for a message.
		StructuredMessage response = queue.receive(true);
		if (response == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return false;
		} else {
			// We have one now, so let's try to process it.
			if (logger.isDebugEnabled()) {
				logger.debug("Received message: " + response.toString());
			}
			if ("Result".equals(response.getContentClass())) {
				processContent(response);
			} else if ("ApiResponse".equals(response.getContentClass())) {
				ApiResponseMessage apiResponse = response.decodeContent(ApiResponseMessage.class);
				logger.error("Got an API response back: " + apiResponse.toString());
				if ("error".equalsIgnoreCase(apiResponse.getType())) {
					processError(response);
				}
			} else {
				logger.error(String.format("Unexpected content class: class=%s, content=%s", response.getContentClass(),
						response.getContent()));
			}
			return true;
		}
	}

	/**
	 * Handle storage of inventory data.
	 */
	private void processContent(StructuredMessage inventoryResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing data series response: %s", inventoryResponse.toString()));
		String type = inventoryResponse.getType();
		String operation = inventoryResponse.getOperation();
		String tenantId = inventoryResponse.getTenantId();
		String dataSeriesId = inventoryResponse.getXactTag();
		if (!"process".equals(operation)) {
			logger.error("ABORT: Expected a process operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a process response");
			return;
		}

		// Decode the response.
		Result[] results = inventoryResponse.decodeContent(new Result[0].getClass());

		// Start a transaction to upsert inventory records, then update the data series
		// record.
		DataSeriesDataService dsDataService = new DataSeriesDataService();
		HostDataService hostDataService = new HostDataService();
		Session session = null;
		try {
			session = context.tenantSession(tenantId).enlist(dsDataService).enlist(hostDataService);
			// See if we have a matching data series. If not, the transaction must be
			// aborted.
			DataSeries dataSeries = dsDataService.get(dataSeriesId);
			if (dataSeries == null) {
				throw new Exception(String.format(
						"Data Series ID does not exist, discarding response: tenantId=%s, dataSeriesId=%s", tenantId,
						dataSeriesId));
			}

			// Deserialize and add each result we recognize.
			boolean deleteObsoleteHostRecords = true;
			for (Result result : results) {
				if ("Host".equals(result.getResultType())) {
					// Ensure any host records from this data series are deleted before adding new
					// ones.
					if (deleteObsoleteHostRecords) {
						int deleted = hostDataService.deleteByDataSeriesId(dataSeriesId);
						logger.info(String.format("Deleting obsolete host records: dataSeriesId=%s, deleted=%d",
								dataSeriesId, deleted));
						deleteObsoleteHostRecords = false;
					}

					Host host = JsonHelper.readFromString(result.getData(), Host.class);
					hostDataService.create(host);
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Adding host inventory record: resource=%s", host.getHostId(),
								host.getResourceId(), host.getEffectiveDate()));
					}
				} else {
					throw new Exception(
							String.format("Unrecognized result type: resultType=%s, tenantId=%s, dataSeriesId=%s",
									result.getResultType(), tenantId, dataSeriesId));
				}
			}

			// Update the data series.
			dataSeries.setState(StateEnum.PROCESSED);
			dsDataService.update(dataSeries.getId().toString(), dataSeries);

			// Great, we're done. Commit the whole thing and head for home.
			session.commit();
			logger.info(String.format("Successfully added data series response: tenantId=%s, dataSeriesId=%s", tenantId,
					dataSeries.getId().toString()));
		} catch (Exception e) {
			logger.error(String.format("Transaction failed: %s", e.getMessage()), e);
			if (session != null)
				logger.debug("Rolling back session");
			session.rollback();
		}

	}

	/**
	 * Handle a data series processing error
	 */
	private void processError(StructuredMessage inventoryResponse) {
		// Fetch header information and validate information.
		logger.info(String.format("Processing inventory response: %s", inventoryResponse.toString()));
		String type = inventoryResponse.getType();
		String operation = inventoryResponse.getOperation();
		String tenantId = inventoryResponse.getTenantId();
		String dataSeriesId = inventoryResponse.getXactTag();
		if (!"process".equals(operation)) {
			logger.error("ABORT: Expected a process operation");
			return;
		}
		if (!"response".equals(type)) {
			logger.error("ABORT: Expected a process response");
			return;
		}

		// Decode the response message.
		ApiResponseMessage apiResponse = inventoryResponse.decodeContent(ApiResponseMessage.class);

		// Start a transaction to update the data series with ERROR state.
		DataSeriesDataService dsDataService = new DataSeriesDataService();
		try (Session session = context.tenantSession(tenantId).enlist(dsDataService)) {
			// See if we have a data series. If not, the transaction must be aborted.
			DataSeries dataSeries = dsDataService.get(dataSeriesId);
			if (dataSeries == null) {
				logger.error(String.format(
						"Data Series ID does not exist, discarding error response: tenantId=%s, dataSeriesId=%s",
						tenantId, dataSeriesId));
				session.rollback();
				return;
			}

			// Update the data series.
			dataSeries.setState(StateEnum.ERROR);
			dsDataService.update(dataSeriesId, dataSeries);

			// Commit.
			session.commit();
			logger.info(String.format("Logged data series processing error: tenantId=%s, dataSeriesId=%s, message=%s",
					tenantId, dataSeriesId, apiResponse.getMessage()));
		}
	}
}