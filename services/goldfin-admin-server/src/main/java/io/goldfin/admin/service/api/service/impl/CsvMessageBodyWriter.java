/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Writes the body of a CSV String to UTF-8 encoded bytes so that it may be
 * included in an HTTP response body. This class must be in the classpath to
 * return CSV file responses.
 */
@Provider
@Produces("text/csv")
public class CsvMessageBodyWriter implements MessageBodyWriter<String> {
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (type == String.class) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public long getSize(String csv, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// Deprecated; return -1 per Jax RS Javadoc recommendation.
		return -1;
	}

	@Override
	public void writeTo(String csv, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		if (csv != null) {
			byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
			entityStream.write(bytes);
		}
	}
}