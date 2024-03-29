/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.tasks;

/**
 * Denotes a class that receives progress updates. 
 */
public interface ProgressReporter {
	public void progress(String description, double percent);
}