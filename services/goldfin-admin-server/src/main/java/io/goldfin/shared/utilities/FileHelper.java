/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Useful routines related to files.
 */
public class FileHelper {
	static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

	/**
	 * Return the home directory of the active service.
	 */
	public static File homeDir() {
		String homeDirName = System.getProperty("api.server.home.dir");
		if (homeDirName == null) {
			homeDirName = ".";
		}
		return new File(homeDirName);
	}

	/**
	 * Finds a configuration file. The following directories are checked in priority
	 * order.
	 * <ol>
	 * <li>GOLDFIN_CONFIG_DIR environmental variable</li>
	 * <li>/var/lib/goldfin/conf</li>
	 * <li>${api.server.home.dir}/conf</li>
	 * </ol>
	 * 
	 * @param name
	 *            Parameter file name
	 * @return File if found or null
	 */
	public static File getConfigFile(String name) {
		// Create the paths.
		List<File> configPath = new ArrayList<File>();
		String goldfinConfigDir = System.getenv("GOLDFIN_CONFIG_DIR");
		if (goldfinConfigDir != null) {
			configPath.add(new File(goldfinConfigDir));
		}
		configPath.add(new File("/var/lib/goldfin/conf"));
		configPath.add(new File(homeDir(), "conf"));

		// Now look for the file.
		for (File configDir : configPath) {
			File config = new File(configDir, name);
			logger.info(String.format("Seeking config file: %s", config.getAbsolutePath()));
			if (config.isFile() && config.canRead()) {
				return config;
			}
		}

		return null;
	}

	/**
	 * Create an empty directory, removing any previous contents.
	 */
	public static File resetDirectory(File dir) {
		remove(dir);
		dir.mkdirs();
		if (!dir.isDirectory()) {
			throw new RuntimeException("Unable to create directory: " + dir.getAbsolutePath());
		}
		return dir;
	}

	/** Remove a file or directory. */
	public static void remove(File fileOrDir) {
		if (fileOrDir.exists()) {
			if (fileOrDir.isDirectory()) {
				// Recursively remove children.
				for (File child : fileOrDir.listFiles()) {
					remove(child);
				}
			}
			fileOrDir.delete();
		}

		if (fileOrDir.exists()) {
			throw new RuntimeException("Unable to remove file: " + fileOrDir.getAbsolutePath());
		}
	}

	/** Write one or more lines to a file. */
	public static File writeLines(File f, String... lines) throws IOException {
		String lineSeparator = System.getProperty("line.separator");
		try (FileWriter fw = new FileWriter(f)) {
			for (String line : lines) {
				fw.write(line);
				fw.write(lineSeparator);
			}
			fw.flush();
			return f;
		}
	}

	/** Write binary data to a file. */
	public static File writeBytes(File f, byte[] data) throws IOException {
		try (FileOutputStream out = new FileOutputStream(f)) {
			for (int i = 0; i < data.length; i++) {
				out.write(data[i]);
			}
			out.flush();
		}
		return f;
	}
}
