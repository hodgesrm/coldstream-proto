/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import io.goldfin.shared.utilities.JsonHelper;

/**
 * Implements a simple REST client library that exchanges JSON-encoded messages
 * using Apache HTTP Client.
 */
public class MinimalRestClient {
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
		// Set the content type to 'application/json' for all requests.
		defaultHeader("Content-Type", "application/json");
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
			uriRequest = new HttpGet(url);
			break;
		case POST:
			uriRequest = new HttpPost(url);
			if (restRequest.hasHttpEntity()) {
				((HttpPost) uriRequest).setEntity(restRequest.getHttpEntity());
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
				if (restRequest.contentString != null) {
					println("Content:");
					println(restRequest.contentString);
				} else if (restRequest.contentBytes != null) {
					println("Content:");
					println("(bytes)");
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
			if (entity != null && entity.getContentLength() > 0) {
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
		restRequest.headers.put("ContentType", "application/json");
		try {
			if (requestEntity != null) {
				restRequest.content(requestEntity);
			}
			RestResponse restResponse = this.execute(restRequest);
			if (restResponse.code >= 300) {
				throw new RestException(restResponse.code, restResponse.reason);
			} else {
				return JsonHelper.readFromStream(new ByteArrayInputStream(restResponse.getContent()),
						responseEntityClass);
			}
		} catch (IOException e) {
			throw new RestRuntimeException("REST POST invocation failed", e);
		}
	}

	/**
	 * Post a JSON message and receive a single JSON message in response.
	 */
	public <T> T get(String path, Class<T> responseEntityClass) throws RestException {
		RestRequest restRequest = new RestRequest().GET().path(path);
		restRequest.headers.put("ContentType", "application/json");
		try {
			RestResponse restResponse = this.execute(restRequest);
			if (restResponse.code >= 300) {
				throw new RestException(restResponse.code, restResponse.reason);
			} else {
				return JsonHelper.readFromStream(new ByteArrayInputStream(restResponse.getContent()),
						responseEntityClass);
			}
		} catch (IOException e) {
			throw new RestRuntimeException("REST POST invocation failed");
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
			throw new RestException(restResponse.code, restResponse.reason);
		} else {
			return;
		}
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