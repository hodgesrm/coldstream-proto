/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Useful routines related to YAML files.
 */
public class YamlHelper {
	/**
	 * Deserialize from a YAML file to Java object.
	 */
	public static <T> T readFromFile(File file, Class<T> outputType)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		return mapper.readValue(file, outputType);
	}

	/**
	 * Deserialize from a YAML file on Java resource path to Java object.
	 */
	public static <T> T readFromClasspath(String path, Class<T> outputType)
			throws JsonParseException, JsonMappingException, IOException {
		ClassLoader classLoader = new YamlHelper().getClass().getClassLoader();
		File file = new File(classLoader.getResource(path).getFile());
		return readFromFile(file, outputType);
	}

	/**
	 * Serialize from Java object to a Yaml file.
	 */
	public static void writeToFile(File file, Object o)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.writeValue(file, o);
	}
}