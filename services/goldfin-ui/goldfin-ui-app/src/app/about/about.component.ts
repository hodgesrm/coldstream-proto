/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component } from '@angular/core';

@Component({
    styleUrls: ['./about.component.scss'],
    templateUrl: './about.component.html'
})
export class AboutComponent {
    about_open: Boolean = false;
    what: String = 'some routing';
}
