/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.invoice.core;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.Future;

import io.goldfin.invoice.utilities.YamlHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line tool to implement system commands.
 */
public class SystemCtl {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Get command and arguments.
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		String command = args[0];
		String[] remainder;
		if (args.length > 1) {
			remainder = Arrays.copyOfRange(args, 1, args.length);
		} else {
			remainder = new String[0];
		}

		if ("help".equals(command)) {
			usage();
		} else if ("init".equals(command)) {
			initCommand(remainder);
		} else if ("remove".equals(command)) {
			removeCommand(remainder);
		} else {
			println("Unrecognized command: " + command);
			System.exit(1);
		}
	}

	private static void initCommand(String[] args) {
		OptionParser parser = new OptionParser();
		parser.accepts("init-params", "Initialization parameter file").withRequiredArg().ofType(File.class);
		OptionSet options = parser.parse(args);

		File initParamsFile = (File) options.valueOf("init-params");
		if (initParamsFile == null) {
			error("You must specify a parameter file with --init-params");
		}
		if (!initParamsFile.canRead()) {
			error("Unknown file: " + initParamsFile.getAbsolutePath());
		}

		SystemInitParams params;
		try {
			params = YamlHelper.readFromFile(initParamsFile, SystemInitParams.class);
			SystemInitializer initializer = new SystemInitializer(params);
			Future<TaskStatus> outcome = initializer.initialize();
			TaskStatus status = outcome.get();
			println(status.getOutcome().toString());
			println(status.getMessage());
		} catch (Exception e) {
			println("Error: " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
			error("Failed!");
		}
	}

	private static void removeCommand(String[] args) {
		println("Not implemented");
	}

	private static void usage() {
		println("systemctl <command>");
		println("Commands:");
		println("init   -- Initialize service");
		println("remove -- Remove service");
		println("help   -- Print help");
	}

	private static void println(String msg) {
		System.out.println(msg);
	}

	private static void error(String msg) {
		println(msg);
		System.exit(1);
	}
}
