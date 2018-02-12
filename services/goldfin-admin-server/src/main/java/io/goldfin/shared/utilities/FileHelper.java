/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Useful routines related to files.
 */
public class FileHelper {
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
