/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.dbutils;

import java.io.File;

/**
 * Contains a single SQL batch including the source file name and starting line
 * number.
 */
public class SqlBatch {
	private final File source;
	private final int lineno;
	private final String content;

	public SqlBatch(File source, int lineno, String content) {
		this.source = source;
		this.lineno = lineno;
		this.content = content;
	}

	public File getSource() {
		return source;
	}

	public int getLineno() {
		return lineno;
	}

	public String getContent() {
		return content;
	}

	public String toString() {
		return String.format("%s source=%s, lineno=%d, content=[%s]", this.getClass().getSimpleName(), source, lineno,
				content);
	}
}
