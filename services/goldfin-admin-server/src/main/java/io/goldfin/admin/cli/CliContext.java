/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.cli;

import io.goldfin.admin.http.MinimalRestClient;
import joptsimple.OptionSet;

/**
 * Callback interface to allow commands to do their work.
 */
public interface CliContext {
	public OptionSet options();

	public void setSession(Session session);

	public void clearSession();
	
	public MinimalRestClient getRestClient();
}