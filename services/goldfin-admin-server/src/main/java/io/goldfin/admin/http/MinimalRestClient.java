/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.shared.utilities.JsonHelper;
import io.goldfin.shared.utilities.SerializationException;

/**
 * Implements a simple REST client library that exchanges JSON-encoded messages
 * using Apache HTTP Client.
 */
public class MinimalRestClient {
	static final Logger logger = LoggerFactory.getLogger(MinimalRestClient.class);

	private String host;
	private int port;
	private String prefix = "/";
	private boolean verbose = false;
	private SSLContext sslContext;
	private SSLConnectionSocketFactory socketFactory;
	private CloseableHttpClient httpClient;
	private List<Header> defaultHeaders = new ArrayList<Header>();

	public MinimalRestClient() throws RestRuntimeException {
		// Use a TrustStrategy implementation that allows any X509 cert.
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] certificates, String authenticationType)
						throws CertificateException {
					return true;
				}
			}).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RestRuntimeException(e.getMessage(), e);
		}
		// Allow TLSv1.2 protocol only. Use a dummy host name verifier for now.
		socketFactory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1.2" }, null,
				new HostnameVerifier() {
					@Override
					public boolean verify(String hostName, SSLSession session) {
						return true;
					}
				});
	}

	public MinimalRestClient defaultHeader(String name, String value) {
		Header h = new BasicHeader(name, value);
		defaultHeaders.add(h);
		return this;
	}

	public MinimalRestClient host(String host) {
		this.host = host;
		return this;
	}

	public MinimalRestClient port(int port) {
		this.port = port;
		return this;
	}

	public MinimalRestClient prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public MinimalRestClient verbose(boolean verbose) {
		this.verbose = verbose;
		return this;
	}

	public MinimalRestClient header(String name, String value) {
		Header h = new BasicHeader(name, value);
		defaultHeaders.add(h);
		return this;
	}

	public MinimalRestClient connect() {
		httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		return this;
	}

	public void close() {
		closeQuietly(httpClient);
	}

	public RestResponse execute(RestRequest restRequest) throws RestException {
		// Prepare the request.
		HttpUriRequest uriRequest;
		String url = makeUrl(restRequest.path);
		switch (restRequest.method) {
		case GET:
			try {
				URIBuilder builder = new URIBuilder(url);
				for (String param: restRequest.params.keySet()) {
					builder.addParameter(param, restRequest.params.get(param));
				}
				uriRequest = new HttpGet(builder.build());
			} catch (URISyntaxException e) {
				throw new RestRuntimeException("Invalid URI", e);
			}
			break;
		case POST:
			uriRequest = new HttpPost(url);
			if (restRequest.hasHttpEntity()) {
				((HttpPost) uriRequest).setEntity(restRequest.getHttpEntity());
			}
			break;
		case PUT:
			uriRequest = new HttpPut(url);
			if (restRequest.hasHttpEntity()) {
				((HttpPut) uriRequest).setEntity(restRequest.getHttpEntity());
			}
			break;
		case DELETE:
			uriRequest = new HttpDelete(url);
			break;
		default:
			throw new RestRuntimeException("Missing HTTP method");
		}
		addDefaultHeaders(uriRequest);
		addRequestHeaders(uriRequest, restRequest.headers);

		// Dump request contents if verbose is enabled.
		if (verbose) {
			println(String.format("%s %s", restRequest.method.toString(), url));
			for (Header header : uriRequest.getAllHeaders()) {
				println(String.format("%s: %s", header.getName(), header.getValue()));
			}
			if (verbose) {
				if (restRequest.hasHttpEntity()) {
					HttpEntity httpEntity = restRequest.getHttpEntity();
					println(String.format("Content Length: %d", httpEntity.getContentLength()));
					println(String.format("Content Type: %s", httpEntity.getContentType()));
					println(String.format("Content Encoding: %s", httpEntity.getContentEncoding()));
					println("Content:");
					if (httpEntity instanceof StringEntity) {
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						try {
							((StringEntity) httpEntity).writeTo(output);
							output.close();
							String stringContent = new String(output.toByteArray(), "UTF-8");
							println(stringContent);
						} catch (IOException e) {
							println(String.format("(Unable to print content, msg=%s)", e.toString()));
						}
					} else {
						println("(bytes)");
					}
				}
			}
		}

		// Issue the call and generate a response. All resources related to the call are
		// deallocated before call ends.
		CloseableHttpResponse response = null;
		try {
			// Execute request and collect status/headers.
			response = httpClient.execute(uriRequest);
			StatusLine status = response.getStatusLine();
			RestResponse restResponse = new RestResponse().code(status.getStatusCode())
					.reason(status.getReasonPhrase());
			if (verbose) {
				println(String.format("Code: %d", restResponse.code));
				println(String.format("Reason: %s", restResponse.reason));
			}
			for (Header header : response.getAllHeaders()) {
				restResponse.headers.put(header.getName(), header.getValue());
				if (verbose) {
					println(String.format("%s: %s", header.getName(), header.getValue()));
				}
			}

			// Buffer the entity if we have it.
			HttpEntity entity = response.getEntity();
			//if (entity != null && entity.getContentLength() > 0) {
			if (entity != null) {
				InputStream contentInputStream = entity.getContent();
				ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
				int oneByte;
				while ((oneByte = contentInputStream.read()) != -1) {
					byteOutputStream.write(oneByte);
				}
				restResponse.content(byteOutputStream.toByteArray()).contentLength(entity.getContentLength())
						.contentType(entity.getContentType().getValue());
				if (verbose) {
					if ("application/json".equals(restResponse.contentType)) {
						String contentAsString = new String(restResponse.getContent(), "UTF-8");
						println("Content:");
						println(contentAsString);
					}
				}
			}
			return restResponse;
		} catch (IOException e) {
			throw new RestRuntimeException("REST invocation failed", e);
		} finally {
			this.closeQuietly(response);
		}
	}

	/**
	 * Post a JSON message and receive a single JSON message in response.
	 */
	public <T> T post(String path, Object requestEntity, Class<T> responseEntityClass) throws RestException {
		RestRequest restRequest = new RestRequest().method(RestHttpMethod.POST).path(path);
		try {
			if (requestEntity != null) {
				restRequest.content(requestEntity);
			}
			RestResponse restResponse = this.execute(restRequest);
			if (restResponse.code >= 300) {
				throw generateRestException(restResponse);
			} else {
				if (responseEntityClass == null) {
					return null;
				} else {
					return JsonHelper.readFromStream(new ByteArrayInputStream(restResponse.getContent()),
							responseEntityClass);
				}
			}
		} catch (SerializationException e) {
			throw new RestRuntimeException("Serialization error", e);
		}
	}

	/**
	 * Get a single JSON message from a path.
	 */
	public <T> T get(String path, Class<T> responseEntityClass) throws RestException {
		RestRequest restRequest = new RestRequest().GET().path(path);
		restRequest.headers.put("ContentType", "application/json");
		try {
			RestResponse restResponse = this.execute(restRequest);
			if (restResponse.code >= 300) {
				throw generateRestException(restResponse);
			} else {
				return JsonHelper.readFromStream(new ByteArrayInputStream(restResponse.getContent()),
						responseEntityClass);
			}
		} catch (SerializationException e) {
			throw new RestRuntimeException("Serialization error", e);
		}
	}

	/**
	 * Update a resource with a JSON document at a specific path. 
	 */
	public void update(String path, Object requestEntity) throws RestException {
		RestRequest restRequest = new RestRequest().method(RestHttpMethod.PUT).path(path);
		try {
			if (requestEntity != null) {
				restRequest.content(requestEntity);
			}
			RestResponse restResponse = this.execute(restRequest);
			if (restResponse.code >= 300) {
				throw generateRestException(restResponse);
			} else {
				return;
			}
		} catch (SerializationException e) {
			throw new RestRuntimeException("Serialization error", e);
		}
	}

	/**
	 * Delete a resource.
	 */
	public void delete(String path) throws RestException {
		RestRequest restRequest = new RestRequest().DELETE().path(path);
		restRequest.headers.put("ContentType", "application/json");
		RestResponse restResponse = this.execute(restRequest);
		if (restResponse.code >= 300) {
			throw generateRestException(restResponse);
		} else {
			return;
		}
	}

	private RestException generateRestException(RestResponse response) throws RestException {
		// Try to get the error message.
		String message = null;
		try {
			ApiResponseMessage apiResponse = JsonHelper.readFromStream(new ByteArrayInputStream(response.getContent()),
					ApiResponseMessage.class);
			message = apiResponse.getMessage();
		} catch (SerializationException e) {
			logger.debug("Unable to deserialize error response", e);
		}
		throw new RestException(response.code, response.reason, message);
	}

	private String makeUrl(String path) {
		return String.format("https://%s:%d%s%s", host, port, prefix, path);
	}

	private void addRequestHeaders(HttpUriRequest request, Map<String, String> requestHeaders) {
		for (Entry<String, String> header : requestHeaders.entrySet()) {
			request.addHeader(header.getKey(), header.getValue());
		}
	}

	private void addDefaultHeaders(HttpUriRequest request) {
		for (Header header : defaultHeaders) {
			request.addHeader(header);
		}
	}

	private void println(String msg) {
		System.out.println(msg);
	}

	private void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				// Ignore
			}
		}
	}
}