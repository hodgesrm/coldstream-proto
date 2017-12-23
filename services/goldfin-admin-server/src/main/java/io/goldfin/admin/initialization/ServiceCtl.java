/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.initialization;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.goldfin.shared.config.SystemInitParams;
import io.goldfin.shared.tasks.ProgressReporter;
import io.goldfin.shared.tasks.TaskStatus;
import io.goldfin.shared.utilities.YamlHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line tool to implement service management operations.
 */
public class ServiceCtl {
	private OptionParser parser = new OptionParser();
	private OptionSet options;

	/**
	 * Process a service management request.
	 */
	public void run(String[] args) {
		// Get command and arguments.
		if (args.length == 0) {
			usage();
			return;
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
		} else if ("create".equals(command)) {
			createCommand(remainder);
		} else if ("remove".equals(command)) {
			removeCommand(remainder);
		} else {
			error("Unrecognized command: " + command);
		}
	}

	/**
	 * Processes a service create command.
	 */
	private void createCommand(String[] args) {
		parser.accepts("init-params", "Initialization parameter file").withRequiredArg().ofType(File.class);
		parser.accepts("dbms-config", "Generated DBMS connection parameters file").withRequiredArg().ofType(File.class);
		if (!parseOptions("create", args)) {
			return;
		}

		File initParamsFile = (File) options.valueOf("init-params");
		if (initParamsFile == null) {
			error("You must specify a parameter file with --init-params");
		}
		if (!initParamsFile.canRead()) {
			error("Unknown file: " + initParamsFile.getAbsolutePath());
		}
		File dbmsConfigFile = (File) options.valueOf("dbms-config");
		if (dbmsConfigFile == null) {
			error("You must specify a connection parameter file name with --dbms-config");
		}

		SystemInitParams params;
		try {
			params = YamlHelper.readFromFile(initParamsFile, SystemInitParams.class);
			ServiceManager initializer = new ServiceManager(params);
			initializer.addProgressReporter(createProgressReporter());
			Future<TaskStatus> outcome = initializer.create(dbmsConfigFile);
			processOutcome(outcome);
		} catch (Exception e) {
			error("Operation failed", e);
		}
	}

	private void removeCommand(String[] args) {
		parser.accepts("init-params", "YAML service parameter file").withRequiredArg().ofType(File.class);
		parser.accepts("ignore-errors", "Ignore script errors");
		if (!parseOptions("create", args)) {
			return;
		}

		File initParamsFile = (File) options.valueOf("init-params");
		boolean ignoreErrors = options.has("ignore-errors");
		if (initParamsFile == null) {
			error("You must specify a parameter file with --init-params");
		}
		if (!initParamsFile.canRead()) {
			error("Unknown file: " + initParamsFile.getAbsolutePath());
		}

		SystemInitParams params;
		try {
			params = YamlHelper.readFromFile(initParamsFile, SystemInitParams.class);
			ServiceManager initializer = new ServiceManager(params);
			initializer.addProgressReporter(createProgressReporter());
			Future<TaskStatus> outcome = initializer.remove(ignoreErrors);
			processOutcome(outcome);
		} catch (Exception e) {
			error("Operation failed", e);
		}
	}

	private void usage() {
		println("systemctl <command>");
		println("Commands:");
		println("create -- Create service");
		println("remove -- Remove service");
		println("help   -- Print help");
	}

	private boolean parseOptions(String command, String[] args) {
		parser.acceptsAll(Arrays.asList("help", "h"), "Print usage information").forHelp();
		options = parser.parse(args);
		if (options.has("h") || options.has("help")) {
			try {
				parser.printHelpOn(System.out);
				return false;
			} catch (IOException e) {
				throw new CommandError("Unable to print help", e);
			}
		}
		return true;
	}

	private ProgressReporter createProgressReporter() {
		return new ProgressReporter() {
			@Override
			public void progress(String description, double percent) {
				String message = String.format("[%6.2f%%]: [%s]", percent, description);
				println(message);
			}
		};
	}

	private void processOutcome(Future<TaskStatus> outcome) throws InterruptedException, ExecutionException {
		TaskStatus status = outcome.get();
		if (status.failed()) {
			error(status.getMessage(), status.getThrowable());
		} else {
			println(status.getOutcome().toString());
			println(status.getMessage());
			if (status.getThrowable() != null) {
				status.getThrowable().printStackTrace();
			}
		}
	}

	private void error(String msg) {
		throw new CommandError(msg);
	}

	private void error(String msg, Throwable cause) {
		throw new CommandError(msg, cause);
	}

	private static void println(String msg) {
		System.out.println(msg);
	}
}