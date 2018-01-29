/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
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
import java.security.MessageDigest;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Document.StateEnum;
import io.goldfin.admin.service.api.model.User;
import io.goldfin.shared.cloud.CloudConnectionFactory;
import io.goldfin.shared.cloud.StorageConnection;
import io.goldfin.shared.crypto.Sha256HashingAlgorithm;
import io.goldfin.shared.data.Session;
import io.goldfin.tenant.data.DocumentDataService;

/**
 * Handles operations related to documents.
 */
public class DocumentManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(DocumentManager.class);
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

	public Document createDocument(Principal principal, InputStream content, String fileName, String description,
			String contentType) throws IOException {
		String tenantId = getTenantId(principal);
		DocumentDataService docService = new DocumentDataService();

		// Download the document into a temporary file.
		File tempFile = Files.createTempFile(tenantId, fileName, new FileAttribute<?>[0]).toFile();
		long contentLength = 0;
		MessageDigest digest = Sha256HashingAlgorithm.getMessageDigest();

		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			byte[] buf = new byte[1024];
			int len;
			while ((len = content.read(buf)) >= 0) {
				fos.write(buf, 0, len);
				// Keep adding to digest and buffer length.
				contentLength += len;
				digest.update(buf);
			}
			fos.close();
		} catch (IOException e) {
			throw e;
		}

		// Convert the SHA-256 digest to a string.
		byte[] digestValue = digest.digest();
		String sha256 = Sha256HashingAlgorithm.bytesToHexString(digestValue);

		// See if the thumbprint already exists.
		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			Document doc = docService.getByThumbprint(sha256);
			if (doc != null) {
				throw new InvalidInputException(
						String.format("Document already exists: id=%s", doc.getId().toString()));
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

		// Store the document descriptor.
		Document document = new Document();
		document.setId(docId);
		document.setName(fileName);
		document.setDescription(description);
		document.setContentType(contentType);
		document.setContentLength(BigDecimal.valueOf(contentLength));
		document.setThumbprint(sha256);
		document.setLocator(locator);
		document.setState(StateEnum.CREATED);

		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			String id = docService.create(document);
			session.commit();
			logger.info("Document created: id=" + id);
		}

		// All done!
		return this.getDocument(principal, document.getId().toString());
	}

	public void deleteDocument(Principal principal, String id) {
		// Find the document to ensure it exists.
		Document document = getDocument(principal, id);

		// Delete the document from storage. This has to go first so
		// we don't lose the document.
		String tenantId = getTenantId(principal);
		StorageConnection connection = CloudConnectionFactory.getInstance().getStorageConnection();
		connection.deleteTenantDocument(tenantId, document.getId().toString());

		// Now erase document metadata.
		DocumentDataService docService = new DocumentDataService();
		try (Session session = context.tenantSession(tenantId).enlist(docService)) {
			int rows = docService.delete(id);
			session.commit();
			if (rows == 0) {
				// Could happen due to concurrent access.
				throw new EntityNotFoundException("Document does not exist");
			}
		}
	}

	public Document getDocument(Principal principal, String id) {
		String tenantId = getTenantId(principal);
		DocumentDataService documentDataService = new DocumentDataService();
		try (Session session = context.tenantSession(tenantId).enlist(documentDataService)) {
			Document documentEnvelope = documentDataService.get(id);
			if (documentEnvelope == null) {
				throw new EntityNotFoundException("Document does not exist");
			}
			return documentEnvelope;
		}
	}

	public List<Document> getAllDocuments(Principal principal) {
		String tenantId = getTenantId(principal);
		DocumentDataService documentService = new DocumentDataService();
		try (Session session = context.tenantSession(tenantId).enlist(documentService)) {
			return documentService.getAll();
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