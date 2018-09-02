/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Component, Input, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { User } from '../services/user';
import { Configuration } from '../client/configuration';

@Component({
    selector: 'login', 
    styleUrls: ['./login.component.scss'],
    templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit, AfterViewInit {
  open: Boolean = false;
  error_message: String;
  title: string = 'Goldfin';

  // Fields from screen. 
  user: User;

  constructor(
    private router: Router,
    private configuration: Configuration,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.user = {name: null, password: null};
  }

  // Clear all auth information when the login screen displays.
  ngAfterViewInit(): void {
    this.configuration.username = "";
    this.configuration.apiKey = "";
    this.authService.clearSession();
  }

  login(): void {
    if (this.user.name == null || this.user.name === "") {
      this.error_message = "Please enter a user name";
    } else if (this.user.password == null || this.user.password === "") {
      this.error_message = "Please enter a password";
    } else {
      this.authService.authorize(this.user.name, this.user.password)
        .subscribe(
          session => {
            if (session == null) {
              this.error_message = "Unable to get server session";
              console.log(this.error_message);
            } else {
              console.log("Logged in: " + this.user.name);
              this.error_message = null;
              this.router.navigate(['/home']);
            }
          }, 
          error => {
            console.log(error);
            this.error_message = error.message;
            console.log(this.error_message);
          });
    }
  }
}
