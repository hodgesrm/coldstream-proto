/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import io.goldfin.shared.utilities.YamlHelper;

/**
 * Implementations operations on S3 buckets.
 */
public class S3Connection {
	static final Logger logger = LoggerFactory.getLogger(S3Connection.class);

	/**
	 * Auto-closable wrapper for Amazon S3 client to enable operations to allocate
	 * and free resources using a try block.
	 */
	class StorageConnectionWrapper implements AutoCloseable {
		S3ConnectionParams connectionParams;
		AmazonS3 client;

		StorageConnectionWrapper() {
			try {
				connectionParams = YamlHelper.readFromFile(new File("conf/storage.yaml"), S3ConnectionParams.class);
				BasicAWSCredentials credentials = new BasicAWSCredentials(connectionParams.getAccessKeyId(),
						connectionParams.getSecretAccessKey());
				AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
				client = AmazonS3ClientBuilder.standard().withCredentials(provider)
						.withRegion(connectionParams.getLocation()).build();
			} catch (IOException e) {
				throw new RuntimeException("Unable to connect to S3", e);
			}
		}

		public AmazonS3 getConnection() {
			return client;
		}

		public String getBucket() {
			return connectionParams.getBucket();
		}

		@Override
		public void close() {
			client.shutdown();
		}
	}

	/**
	 * Upload a tenant file to S3.
	 * 
	 * @return Key of file, to be used as locator
	 */
	public String storeTenantDocument(String tenantId, String docId, InputStream input, String fileName,
			String description, String sha256, long contentLength, String contentType) {
		try (StorageConnectionWrapper wrapper = new StorageConnectionWrapper()) {
			String key = String.format("tenant/%s/%s", tenantId, docId);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.addUserMetadata("tenantId", tenantId);
			metadata.addUserMetadata("fileName", fileName);
			metadata.addUserMetadata("description", description);
			metadata.addUserMetadata("sha256", sha256);
			PutObjectRequest request = new PutObjectRequest(wrapper.getBucket(), key, input, metadata);

			logger.info(String.format("Storing document: tenantId=%s, docId=%s, fileName=%s, description=%s", tenantId,
					docId, fileName, description));
			System.out.println("Uploading a new object to S3 from a file\n");
			wrapper.client.putObject(request);

			// Return an S3 URL.
			return wrapper.client.getUrl(wrapper.getBucket(), key).toString();
		} catch (AmazonServiceException e) {
			return handleException(String.format("S3 upload failed: tenantId=%s, docId=%s, fileName=%s, description=%s",
					tenantId, docId, fileName, description), e);
		}
	}

	/**
	 * Delete a tenant file from S3.
	 */
	public void deleteTenantDocument(String tenantId, String docId) {
		try (StorageConnectionWrapper wrapper = new StorageConnectionWrapper()) {
			String key = tenantDocumentKey(tenantId, docId);
			DeleteObjectRequest request = new DeleteObjectRequest(wrapper.getBucket(), key);
			logger.info(String.format("Deleting document: tenantId=%s, docId=%s", tenantId, docId));
			wrapper.client.deleteObject(request);
		} catch (AmazonServiceException e) {
			handleException(String.format("S3 deletion failed: tenantId=%s, docId=%s", tenantId, docId), e);
		}
	}

	private String tenantDocumentKey(String tenantId, String docId) {
		return String.format("tenant/%s/%s", tenantId, docId);
	}

	// Handle exception with dummy return value.
	private String handleException(String message, AmazonServiceException e) {
		logger.error(message, e);
		logger.error("Error Message: " + e.getMessage());
		logger.error("HTTP Status Code: " + e.getStatusCode());
		logger.error("AWS Error Code: " + e.getErrorCode());
		logger.error("Error Type: " + e.getErrorType());
		logger.error("Request ID: " + e.getRequestId());
		throw new RuntimeException(message, e);
	}
}