/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import io.goldfin.admin.exceptions.CommandError;
import io.goldfin.admin.http.MinimalRestClient;
import io.goldfin.admin.restapi.jetty.SecurityAuthenticator;
import io.goldfin.shared.utilities.YamlHelper;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line tool to implement service management operations.
 */
public class SvcClient implements CliContext {
	private Map<String, Command> commands = new TreeMap<String, Command>();
	private OptionSet options;
	private File sessionFile = new File("svc-client-session.yaml");

	public SvcClient() {
		loadCommand(new CmdDataCreate());
		loadCommand(new CmdDataDelete());
		loadCommand(new CmdDataList());
		loadCommand(new CmdDataProcess());
		loadCommand(new CmdDocumentCreate());
		loadCommand(new CmdDocumentDelete());
		loadCommand(new CmdDocumentList());
		loadCommand(new CmdDocumentProcess());
		loadCommand(new CmdExtractDownload());
		loadCommand(new CmdHostList());
		loadCommand(new CmdInvoiceDelete());
		loadCommand(new CmdInvoiceList());
		loadCommand(new CmdInvoiceValidate());
		loadCommand(new CmdLogin());
		loadCommand(new CmdLogout());
		loadCommand(new CmdTenantCreate());
		loadCommand(new CmdTenantDelete());
		loadCommand(new CmdTenantList());
		loadCommand(new CmdUserCreate());
		loadCommand(new CmdUserDelete());
		loadCommand(new CmdUserList());
		loadCommand(new CmdVendorDelete());
		loadCommand(new CmdVendorList());
	}

	private void loadCommand(Command cmd) {
		commands.put(cmd.getName(), cmd);
	}

	/**
	 * Process a service management request.
	 */
	public void run(String[] args) {
		// Look for the command name.
		if (args.length == 0) {
			throw new CommandError("Missing command");
		}
		String commandName = args[0];
		if ("-h".equals(commandName) || "--help".equals(commandName)) {
			programUsage();
			return;
		}
		Command command = commands.get(commandName);
		if (command == null) {
			throw new CommandError(String.format("Unknown command: %s", commandName));
		}

		// Add options to parser and parse arguments.
		OptionParser parser = command.getOptParser();
		parser.acceptsAll(Arrays.asList("h", "help"), "Print help").forHelp();
		parser.accepts("verbose", "Give extra information about calls");
		String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
		options = parser.parse(commandArgs);

		// See if the user wants help.
		if (options.has("h") || options.has("help")) {
			commandUsage(command);
			return;
		}

		// Execute the command.
		command.exec(this);
	}

	/** Print program commands with nice formatting. */
	private void programUsage() {
		println("Usage: svc-client command [options]");
		println("Commands:");
		// Format the command short names to match the max name length.
		int maxLen = 0;
		for (String name : commands.keySet()) {
			maxLen = Math.max(maxLen, name.length());
		}
		String format = String.format("  %%-%ds  %%s", maxLen);
		for (Command command : commands.values()) {
			println(String.format(format, command.getName(), command.getDescription()));
		}
		println("Use -h or --help to get help on commands");
	}

	/** Print usage for one command. */
	private void commandUsage(Command command) {
		println(String.format("%s: %s", command.getName(), command.getDescription()));
		try {
			command.getOptParser().printHelpOn(System.out);
		} catch (IOException e) {
			println("Error printing help!");
			e.printStackTrace();
		}
	}

	private void println(String msg) {
		System.out.println(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.cli.CliContext#options()
	 */
	@Override
	public OptionSet options() {
		return options;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.cli.CliContext#setSession(io.goldfin.admin.cli.Session)
	 */
	@Override
	public void setSession(Session session) {
		try {
			YamlHelper.writeToFile(sessionFile, session);
		} catch (IOException e) {
			throw new CommandError("Unable to write session file: " + sessionFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.cli.CliContext#clearSession()
	 */
	@Override
	public void clearSession() {
		sessionFile.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.cli.CliContext#getRestClient()
	 */
	@Override
	public MinimalRestClient getRestClient() {
		if (!sessionFile.canRead()) {
			throw new CommandError("Session file does not exist, must login first");
		}
		try {
			Session session = YamlHelper.readFromFile(sessionFile, Session.class);
			return new MinimalRestClient().host(session.getHost()).port(session.getPort()).prefix("/api/v1")
					.verbose(options.has("verbose")).header(SecurityAuthenticator.API_KEY_HEADER, session.getToken())
					.connect();
		} catch (IOException e) {
			throw new CommandError("Unable to read session file: " + sessionFile.getAbsolutePath(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.cli.CliContext#getSession()
	 */
	@Override
	public Session getSession() {
		if (!sessionFile.canRead()) {
			return null;
		} else {
			try {
				return YamlHelper.readFromFile(sessionFile, Session.class);
			} catch (IOException e) {
				throw new CommandError("Unable to read session file: " + sessionFile.getAbsolutePath(), e);
			}
		}
	}
}