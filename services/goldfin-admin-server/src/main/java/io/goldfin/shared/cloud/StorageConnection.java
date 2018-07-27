/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.cloud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Implements operations on S3. Basic principles of operation:
 * <ul>
 * <li>At session creation time clients provide Amazon parameters (AwsParams)
 * plus a bucket, which is used to compute a unique bucket name within the
 * service.</li>
 * <li>The return value from a call to store a file is a locator. Clients should
 * treat it as an opaque string.</li>
 * <li>Operations to get and delete files work on locators. Clients do not need
 * to supply any further information, because the locator has all information
 * required to locate the file.</li>
 * </ul>
 */
public class StorageConnection {
	static final Logger logger = LoggerFactory.getLogger(StorageConnection.class);

	// Connection parameters.
	private final AwsParams connectionParams;
	// A short form of the bucket name that is unique within the service and serves
	// as the basis of the full bucket name.
	private final String bucketHandle;

	/**
	 * Class to parse S3 locator values, which are URLs with the following form:
	 * <p/>
	 * https://<em>bucket</em>.s3.<em>region</em>.amazon.com/<em>key</em>
	 */
	public static class Locator {
		URL locatorURL;
		String bucket;
		String region;
		String key;

		public Locator(String locator) throws MalformedURLException {
			locatorURL = new URL(locator);
			parse();
		}

		private void parse() {
			key = locatorURL.getPath().substring(1);
			String[] hostComponents = locatorURL.getHost().split("\\.");
			bucket = hostComponents[0];
			region = hostComponents[2];
		}

		public String getBucket() {
			return bucket;
		}

		public String getRegion() {
			return region;
		}

		public String getKey() {
			return key;
		}

		public String getLocator() {
			return locatorURL.toString();
		}

		@Override
		public String toString() {
			return getLocator();
		}
	}

	/**
	 * Auto-closable wrapper for Amazon S3 client to enable operations to allocate
	 * and free resources using a try block.
	 */
	class S3ClientWrapper implements AutoCloseable {
		AwsParams params;
		AmazonS3 client;
		String bucket;
		String region;

		/**
		 * Instantiate a new wrapper.
		 * 
		 * @param bucket
		 *            Bucket to use for calls.
		 * @param region
		 *            Region to use for calls.
		 */
		S3ClientWrapper(String bucket, String region) {
			this.bucket = bucket;
			this.region = region;
			BasicAWSCredentials credentials = new BasicAWSCredentials(connectionParams.getAccessKeyId(),
					connectionParams.getSecretAccessKey());
			AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
			client = AmazonS3ClientBuilder.standard().withCredentials(provider).withRegion(region).build();
		}

		public AmazonS3 getConnection() {
			return client;
		}

		public String getBucket() {
			return bucket;
		}

		@Override
		public void close() {
			client.shutdown();
		}
	}

	/**
	 * Starts a new storage connection.
	 * 
	 * @param connectionParams
	 *            Amazon connection parameters.
	 * @param bucketHandle
	 *            A partial name used to compute the full bucket name.
	 */
	public StorageConnection(AwsParams connectionParams, String bucketHandle) {
		this.bucketHandle = bucketHandle;
		this.connectionParams = connectionParams;
	}

	private String computeBucketName() {
		return String.format("%s-%s-%s", connectionParams.getGroup(), bucketHandle, connectionParams.getS3Root());
	}

	/**
	 * Upload a tenant file to S3. This is a wrapper that structures the file key
	 * and metadata before calling a more basic method to upload metadata.
	 * 
	 * @param tenantId
	 *            UUID of tenant
	 * @param docId
	 *            UUID of document
	 * @param input
	 *            InputString containing content
	 * @param fileName
	 *            Name of base file
	 * @param description
	 *            Description of the file or null to omit
	 * @param sha256
	 *            SHA-256 key of file
	 * @param contentLength
	 *            Content length in bytes
	 * @param contentType
	 *            Media type for downloads
	 *
	 * @return URL of file to be used as a storage locator.
	 */
	public String storeTenantDocument(String tenantId, String docId, InputStream input, String fileName,
			String description, String sha256, long contentLength, String contentType) {
		Map<String, String> metadata = new TreeMap<String, String>();
		metadata.put("tenantId", tenantId);
		metadata.put("fileName", fileName);
		metadata.put("description", description);
		metadata.put("sha256", sha256);
		String key = String.format("tenant/%s/%s", tenantId, docId);

		return storeDocument(key, metadata, input);
	}

	/**
	 * Upload a file to S3 using default bucket and region for the connection.
	 * 
	 * @param documentKey
	 *            Key of document
	 * @param docMetadata
	 *            Map containing metadata key/value pairs
	 * @param input
	 *            Input stream to supply content
	 * 
	 * @return URL of file, to be used as locator.
	 */
	public String storeDocument(String documentKey, Map<String, String> docMetadata, InputStream input) {
		try (S3ClientWrapper wrapper = new S3ClientWrapper(this.computeBucketName(), connectionParams.getRegion())) {
			ObjectMetadata metadata = new ObjectMetadata();
			for (String metaKey : docMetadata.keySet()) {
				metadata.addUserMetadata(metaKey, docMetadata.get(metaKey));
			}
			PutObjectRequest request = new PutObjectRequest(wrapper.getBucket(), documentKey, input, metadata);
			logger.info(constructLogMessage(wrapper.getBucket(), documentKey, docMetadata));
			wrapper.client.putObject(request);

			// Return an S3 URL.
			return wrapper.client.getUrl(wrapper.getBucket(), documentKey).toString();
		} catch (AmazonServiceException e) {
			// Dummy return value to make compiler happy.
			return handleException(String.format("S3 upload failed: key=%s", documentKey), e);
		}
	}

	private String constructLogMessage(String bucket, String key, Map<String, String> meta) {
		StringBuffer metaBuf = new StringBuffer();
		for (String metaKey : meta.keySet()) {
			if (metaBuf.toString().length() > 0) {
				metaBuf.append(",");
			}
			metaBuf.append(String.format("%s=[%s]", metaKey, meta.get(metaKey)));
		}
		return String.format("Storing document: bucket=%s, key=%s, metadata=(%s)", bucket, key, metaBuf.toString());
	}

	/**
	 * Download tenant file content from S3 to a file.
	 * 
	 * @param locator
	 *            Document locator, which in the case of S3 is a URL that we parse
	 *            to find the bucket and key.
	 * @param file
	 *            A file in which to place the document.
	 * @return Number of bytes downloaded
	 */
	public long fetchDocument(String locator, File file) {
		long contentLength = 0;
		try {
			Locator loc = new Locator(locator);
			logger.info(String.format("Fetching document to file: bucket=%s, region=%s, key=%s, file=%s",
					loc.getBucket(), loc.getRegion(), loc.getKey(), file.getAbsolutePath()));
			try (S3ClientWrapper wrapper = new S3ClientWrapper(loc.getBucket(), loc.getRegion())) {
				GetObjectRequest request = new GetObjectRequest(wrapper.getBucket(), loc.getKey());
				S3Object s3Object = wrapper.client.getObject(request);
				InputStream content = s3Object.getObjectContent();
				try (FileOutputStream out = new FileOutputStream(file)) {
					byte[] buf = new byte[1024];
					int transferLength;
					while ((transferLength = content.read(buf)) > -1) {
						out.write(buf, 0, transferLength);
						contentLength += transferLength;
					}
				}
			}
		} catch (MalformedURLException e) {
			handleException(
					String.format("Invalid locator value: locator=%s, file=%s", locator, file.getAbsolutePath()), e);
		} catch (IOException e) {
			handleException(String.format("File storage failed on download: locator=%s, file=%s", locator,
					file.getAbsolutePath()), e);
		} catch (AmazonServiceException e) {
			handleException(String.format("S3 download failed: locator=%s, file=%s", locator, file.getAbsolutePath()),
					e);
		}
		return contentLength;
	}

	/**
	 * Delete a tenant file from S3.
	 */
	public void deleteTenantDocument(String tenantId, String docId) {
		try (S3ClientWrapper wrapper = new S3ClientWrapper(this.computeBucketName(), connectionParams.getRegion())) {
			String key = tenantDocumentKey(tenantId, docId);
			DeleteObjectRequest request = new DeleteObjectRequest(wrapper.getBucket(), key);
			logger.info(String.format("Deleting document: tenantId=%s, docId=%s", tenantId, docId));
			wrapper.client.deleteObject(request);
		} catch (AmazonServiceException e) {
			handleException(String.format("S3 deletion failed: tenantId=%s, docId=%s", tenantId, docId), e);
		}
	}

	/** Delete a document using the locator only. */
	public void deleteDocument(String locator) {
		try {
			Locator loc = new Locator(locator);
			logger.info(String.format("Deleting document: bucket=%s, region=%s, key=%s", loc.getBucket(),
					loc.getRegion(), loc.getKey()));
			try (S3ClientWrapper wrapper = new S3ClientWrapper(loc.getBucket(), loc.getRegion())) {
				DeleteObjectRequest request = new DeleteObjectRequest(wrapper.getBucket(), loc.getKey());
				wrapper.client.deleteObject(request);
			}
		} catch (MalformedURLException e) {
			handleException(String.format("Invalid locator value: locator=%s", locator), e);
		} catch (AmazonServiceException e) {
			handleException(String.format("S3 deletiion failed: locator=%s", locator), e);
		}
	}

	private String tenantDocumentKey(String tenantId, String docId) {
		return String.format("tenant/%s/%s", tenantId, docId);
	}

	// Handle Amazon exception with dummy return value.
	private String handleException(String message, AmazonServiceException e) {
		logger.error(message, e);
		logger.error("Error Message: " + e.getMessage());
		logger.error("HTTP Status Code: " + e.getStatusCode());
		logger.error("AWS Error Code: " + e.getErrorCode());
		logger.error("Error Type: " + e.getErrorType());
		logger.error("Request ID: " + e.getRequestId());
		throw new RuntimeException(message, e);
	}

	// Handle generic exception with dummy return value.
	private String handleException(String message, Exception e) {
		logger.error(message, e);
		throw new RuntimeException(message, e);
	}
}