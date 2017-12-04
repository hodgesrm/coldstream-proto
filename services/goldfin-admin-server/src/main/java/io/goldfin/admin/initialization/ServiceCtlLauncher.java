/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

/**
 * Launch a service command.
 */
public class ServiceCtlLauncher {
	public static void main(String[] args) {
		try {
			new ServiceCtl().run(args);
			System.exit(0);
		} catch (CommandError e) {
			System.err.println("ERROR: " + e.getMessage());
			if (e.getCause() != null) {
				e.getCause().printStackTrace(System.err);
			}
		} catch (Throwable e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}