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
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
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
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
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
			ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			return mapper.writeValueAsString(o);
		} catch (IOException e) {
			throw new SerializationException("Write failed", e);
		}
	}
}