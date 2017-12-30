/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import io.goldfin.admin.exceptions.CommandError;

/**
 * Launch a service command.
 */
public class SvcInitLauncher {
	public static void main(String[] args) {
		try {
			new SvcInit().run(args);
			System.exit(0);
		} catch (CommandError e) {
			System.err.println("ERROR: " + e.getMessage());
			if (e.getCause() != null) {
				e.getCause().printStackTrace(System.err);
			}
			System.exit(1);
		} catch (Throwable e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}