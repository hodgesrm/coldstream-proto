/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
export class FileProgressReporter {
  progress_open: boolean = false;
  progress_pct: number = 0.0;
  termination: String = null;
  message: String = null;

  // Reset to original values for a new upload. 
  reset(): void {
    this.progress_open = false;
    this.progress_pct = 0.0;
    this.termination = null;
    this.message = null;
  }

  // Upload was successful.
  succeeded(message: String): void {
    this.termination = 'succeeded';
    this.message = message;
  }

  // Upload failed.
  failed(message: String): void {
    this.termination = 'failed';
    this.message = message;
  }
}
