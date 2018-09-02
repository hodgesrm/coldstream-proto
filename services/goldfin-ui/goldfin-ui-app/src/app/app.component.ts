import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Configuration } from './client/configuration';
import { AuthService } from './services/auth.service';

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    about_open:Boolean = false;
    logout_open:Boolean = false;

    constructor(
        private router: Router,
        private configuration: Configuration,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
      if (this.authService.loadSession()) {
        console.log("Restored session for user: " + this.configuration.username);
      } else {
        console.log("No session found, routing to login")
        this.router.navigate(['/login']);
      }
    }

    logout(): void {
      this.logout_open = false;
      this.authService.logout();
      this.router.navigate(['/login']);
    }
}
