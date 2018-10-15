import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Configuration } from './client/configuration';
import { AuthService } from './services/auth.service';
import { environment } from '../environments/environment';

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    about_open:Boolean = false;
    logout_open:Boolean = false;
    VERSION: String = environment.version;

    constructor(
        public router: Router,
        private configuration: Configuration,
        private authService: AuthService
    ) {}

    ngOnInit(): void {
      if (this.authService.loadSession()) {
        console.log("Restored session for user: " + this.configuration.username);
        var url: string = window.sessionStorage.getItem('url');
        if (url != null) {
            console.log("Restoring previous route: " + url);
            this.router.navigate([url]);
        }
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
