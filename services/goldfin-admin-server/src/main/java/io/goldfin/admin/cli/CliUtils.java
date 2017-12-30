/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.exceptions.CommandError;
import joptsimple.OptionSet;

/**
 * Utility methods for CLI operations.
 */
public class CliUtils {
	public static Object requiredOption(OptionSet options, String name) {
		Object value = options.valueOf(name);
		if (value == null) {
			throw new CommandError(String.format("Required option missing: %s", name));
		} else {
			return value;
		}
	}
}
