/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.DataSeries.StateEnum;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.StorageConnection;
import io.goldfin.shared.crypto.Sha256HashingAlgorithm;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.DataSeriesDataService;

/**
 * Handles operations related to data series, which are uploads of observations.
 */
public class DataSeriesManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(DataSeriesManager.class);
	private ManagementContext context;

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	@Override
	public void prepare() {
		// Do nothing for now.
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	public DataSeries createDataSeries(Principal principal, InputStream content, String fileName, String description,
			String contentType, Boolean scan) throws IOException {
		String tenantId = getTenantId(principal);
		DataSeriesDataService docService = new DataSeriesDataService();

		// Download the dataSeries into a temporary file.
		File tempFile = Files.createTempFile(tenantId, fileName, new FileAttribute<?>[0]).toFile();
		long contentLength = 0;
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			byte[] buf = new byte[1024];
			int len;
			while ((len = content.read(buf)) >= 0) {
				fos.write(buf, 0, len);
				contentLength += len;
			}
			fos.close();
		} catch (IOException e) {
			throw e;
		}

		// Compute the SHA-256 digest on the file. We could do this
		// in the write loop but this function is unit-tested.
		String sha256 = Sha256HashingAlgorithm.generateHashString(tempFile);

		// See if the thumbprint already exists.
		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			DataSeries doc = docService.getByThumbprint(sha256);
			if (doc != null) {
				throw new InvalidInputException(
						String.format("DataSeries already exists: id=%s", doc.getId().toString()));
			}
		}

		// Now that we have a local file and metadata, upload same to storage.
		UUID docId = UUID.randomUUID();
		String locator = null;
		try (InputStream localInput = new FileInputStream(tempFile)) {
			StorageConnection connection = CloudConnectionFactory.getInstance().getStorageConnection();
			locator = connection.storeTenantDocument(tenantId, docId.toString(), localInput, fileName, description,
					sha256, contentLength, contentType);
		}

		// Store the dataSeries descriptor.
		DataSeries dataSeries = new DataSeries();
		dataSeries.setId(docId);
		dataSeries.setName(fileName);
		dataSeries.setDescription(description);
		dataSeries.setContentType(contentType);
		dataSeries.setContentLength(BigDecimal.valueOf(contentLength));
		dataSeries.setThumbprint(sha256);
		dataSeries.setLocator(locator);
		dataSeries.setState(StateEnum.CREATED);

		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			String id = docService.create(dataSeries);
			session.commit();
			logger.info("DataSeries created: id=" + id);
		}

		// Schedule a scan if desired.
		if (scan != null && scan.booleanValue()) {
			logger.info("Scan requested on new dataSeries: id=" + dataSeries.getId().toString());
			processDataSeries(principal, dataSeries.getId().toString());
		}

		// All done!
		return this.getDataSeries(principal, dataSeries.getId().toString());
	}

	public void processDataSeries(Principal principal, String id) {
		// Find the dataSeries to ensure it exists.
		DataSeries dataSeries = getDataSeries(principal, id);
		String tenantId = getTenantId(principal);

		// Get the OCR manager and submit the dataSeries for scanning.
		DataSeriesAnalysisManager dsaMgr = ManagerRegistry.getInstance().getManager(DataSeriesAnalysisManager.class);
		dsaMgr.process(tenantId, dataSeries);

		// Assuming we're still alive here, update the dataSeries state to show
		// processing has been requested.
		DataSeriesDataService docService = new DataSeriesDataService();
		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			dataSeries.setState(StateEnum.PROCESS_REQUESTED);
			int rows = docService.update(dataSeries.getId().toString(), dataSeries);
			session.commit();
			if (rows == 0) {
				// Could happen due to concurrent access.
				throw new EntityNotFoundException(
						String.format("DataSeries update failed: dataSeriesId=%s", dataSeries.getId().toString()));
			}
		}
	}

	public void deleteDataSeries(Principal principal, String id) {
		// Find the dataSeries to ensure it exists.
		DataSeries dataSeries = getDataSeries(principal, id);

		// Delete the dataSeries from storage. This has to go first so
		// we don't lose the dataSeries.
		String tenantId = getTenantId(principal);
		StorageConnection connection = CloudConnectionFactory.getInstance().getStorageConnection();
		connection.deleteTenantDocument(tenantId, dataSeries.getId().toString());

		// Now erase dataSeries metadata.
		DataSeriesDataService docService = new DataSeriesDataService();
		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			int rows = docService.delete(id);
			session.commit();
			if (rows == 0) {
				// Could happen due to concurrent access.
				throw new EntityNotFoundException("DataSeries does not exist");
			}
		}
	}

	public DataSeries getDataSeries(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		DataSeriesDataService dataSeriesDataService = new DataSeriesDataService();
		try (Session session = context.tenantSession(tenantId).enlist(dataSeriesDataService)) {
			DataSeries dataSeriesEnvelope = dataSeriesDataService.get(id);
			if (dataSeriesEnvelope == null) {
				throw new EntityNotFoundException("DataSeries does not exist");
			}
			return dataSeriesEnvelope;
		}
	}

	public List<DataSeries> getAllDataSeriess(Principal principal) {
		String tenantId = getTenantId(principal);
		DataSeriesDataService dataSeriesService = new DataSeriesDataService();
		try (Session session = context.tenantSession(tenantId).enlist(dataSeriesService)) {
			return dataSeriesService.getAll();
		}
	}

	/** Get the tenant ID for this user. */
	private String getTenantId(Principal principal) {
		UserManager userManager = ManagerRegistry.getInstance().getManager(UserManager.class);
		User user = userManager.getUser(principal.getName());
		if (user == null) {
			throw new RuntimeException(String.format("Unknown user: %s", principal.getName()));
		} else {
			return user.getTenantId().toString();
		}
	}
}