/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Useful routines related to serialization operations.
 */
public class SerializationHelper {
	// UTF-8 character set.
	private static Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * Write string contents to Gzip'ed UTF8 bytes.
	 */
	public static ByteBuffer serializeToGzipBytes(String content) throws IOException {
		// This does not have to be super efficient, just correct.
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutput = new GZIPOutputStream(byteOutput);

		byte[] bytes = content.getBytes(UTF8);
		gzipOutput.write(bytes);
		gzipOutput.flush();
		gzipOutput.close();
		byteOutput.close();
		byte[] gzippedBytes = byteOutput.toByteArray();

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(gzippedBytes.length);
		byteBuf.put(gzippedBytes);
		byteBuf.rewind();
		return byteBuf;
	}

	/**
	 * Read string contents from Gzip'ed UTF8 bytes.
	 */
	public static String deserializefromGzipBytes(ByteBuffer byteBuffer) throws IOException {
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
		GZIPInputStream gzipInput = new GZIPInputStream(byteInput);

		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = gzipInput.read(buf)) >= 0) {
			byteOutput.write(buf, 0, len);
		}
		gzipInput.close();
		byteInput.close();
		byteOutput.close();

		return new String(byteOutput.toByteArray(), UTF8);
	}

}