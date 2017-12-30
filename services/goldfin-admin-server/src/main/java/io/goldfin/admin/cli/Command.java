/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import joptsimple.OptionParser;

/**
 * Denotes a CLI command executor.
 */
public interface Command {
	public String getName();

	public String getDescription();

	public OptionParser getOptParser();

	public void exec(CliContext ctx);
}