/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

/**
 * Useful routines related to JSON files.
 */
public class JsonHelper {
	/**
	 * Deserialize from a JSON string to Java object.
	 */
	public static <T> T readFromString(String string, Class<T> outputType) {
		try {
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			return mapper.readValue(string, outputType);
		} catch (IOException e) {
			throw new SerializationException("Read failed", e);
		}
	}

	/**
	 * Deserialize from a binary stream to Java object.
	 */
	public static <T> T readFromStream(InputStream input, Class<T> outputType) {
		try {
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			return mapper.readValue(input, outputType);
		} catch (IOException e) {
			throw new SerializationException("Read failed", e);
		}
	}

	/**
	 * Deserialize from a JSON file to Java object.
	 */
	public static <T> T readFromFile(File file, Class<T> outputType) {
		try {
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			return mapper.readValue(file, outputType);
		} catch (IOException e) {
			throw new SerializationException("Read failed", e);
		}
	}

	/**
	 * Deserialize from a JSON file on Java resource path to Java object.
	 */
	public static <T> T readFromClasspath(String path, Class<T> outputType) {
		ClassLoader classLoader = new JsonHelper().getClass().getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		return readFromFile(file, outputType);
	}

	/**
	 * Serialize from Java object to an output stream.
	 */
	public static void writeToStream(OutputStream stream, Object o) {
		try {
			ObjectMapper mapper = getObjectMapper();
			mapper.writeValue(stream, o);
		} catch (IOException e) {
			throw new SerializationException("Write failed", e);
		}
	}

	/**
	 * Serialize from Java object to a JSON file.
	 */
	public static void writeToFile(File file, Object o) {
		try {
			ObjectMapper mapper = getObjectMapper();
			mapper.writeValue(file, o);
		} catch (IOException e) {
			throw new SerializationException("Write failed", e);
		}
	}

	/**
	 * Serialize from Java object to a JSON string.
	 */
	public static String writeToString(Object o) {
		try {
			ObjectMapper mapper = getObjectMapper();
			return mapper.writeValueAsString(o);
		} catch (IOException e) {
			throw new SerializationException("Write failed", e);
		}
	}

	/** 
	 * Returns an object mapper that uses custom date formatting so 
	 * that classes like java.util.Date print consistently. 
	 */
	private static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setDateFormat(new ISO8601DateFormat());
		return mapper;
	}
}