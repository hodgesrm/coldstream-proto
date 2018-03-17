/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit, Input } from '@angular/core';
import { Router }   from '@angular/router'

import { ErrorReporter } from './error-reporter';

@Component({
    selector: 'error-modal', 
    templateUrl: './error-modal.component.html', 
    styles: []
})

export class ErrorModalComponent implements OnInit {
  @Input() reporter: ErrorReporter = null;

  ngOnInit() {
  }
}
