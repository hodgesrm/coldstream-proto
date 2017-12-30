/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.http;

import java.io.Closeable;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
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
public class JsonRestClient {
	private String host;
	private int port;
	private String prefix = "/";
	private SSLContext sslContext;
	private SSLConnectionSocketFactory socketFactory;
	private CloseableHttpClient httpClient;
	private List<Header> headers = new ArrayList<Header>();

	public JsonRestClient() throws RestRuntimeException {
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
		header("Content-Type", "application/json");
	}

	public JsonRestClient header(String name, String value) {
		Header h = new BasicHeader(name, value);
		headers.add(h);
		return this;
	}

	public JsonRestClient host(String host) {
		this.host = host;
		return this;
	}

	public JsonRestClient port(int port) {
		this.port = port;
		return this;
	}

	public JsonRestClient prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public JsonRestClient connect() {
		httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		return this;
	}

	public void close() {
		closeQuietly(httpClient);
	}

	public CloseableHttpResponse post(String path, Object requestEntity) throws RestException {
		String url = makeUrl(path);
		HttpPost post = new HttpPost(url);
		addHeaders(post);
		if (requestEntity != null) {
			String jsonEntity;
			try {
				jsonEntity = JsonHelper.writeToString(requestEntity);
				post.setEntity(new StringEntity(jsonEntity, "UTF-8"));
			} catch (IOException e) {
				throw new RestRuntimeException("Unable to serialize request entity", e);
			}
		}
		try {
			return httpClient.execute(post);
		} catch (IOException e) {
			throw new RestRuntimeException("REST invocation failed", e);
		}
	}

	public <T> T post(String path, Object requestEntity, Class<T> responseEntityClass) throws RestException {
		CloseableHttpResponse response = post(path, requestEntity);
		try {
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= 300) {
				throwException(status);
			}
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity == null) {
				return null;
			} else {
				return JsonHelper.readFromStream(responseEntity.getContent(), responseEntityClass);
			}
		} catch (UnsupportedOperationException | IOException e) {
			throw new RestRuntimeException("Unable to deserialize response entity", e);
		} finally {
			this.closeQuietly(response);
		}
	}

	private String makeUrl(String path) {
		return String.format("https://%s:%d%s%s", host, port, prefix, path);
	}

	private void addHeaders(HttpRequestBase request) {
		for (Header header : headers) {
			request.addHeader(header);
		}
	}

	private void throwException(StatusLine status) throws RestException {
		throw new RestException(status.getStatusCode(), status.getReasonPhrase());
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