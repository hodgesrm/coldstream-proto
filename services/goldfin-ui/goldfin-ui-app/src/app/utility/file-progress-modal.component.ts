/*
 * Copyright (c) 2018 Goldfin.io.  All rights reserved.
 */
import { Component, OnInit, Input } from '@angular/core';
import { Router }   from '@angular/router'

import { FileProgressReporter } from './file-progress-reporter';

@Component({
    selector: 'file-progress-modal', 
    templateUrl: './file-progress-modal.component.html', 
    styles: []
})

export class FileProgressModalComponent implements OnInit {
  @Input() reporter: FileProgressReporter = null;

  ngOnInit() {
  }
}
