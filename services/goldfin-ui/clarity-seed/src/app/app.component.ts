import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {
    about_open:Boolean = false;
    logout_open:Boolean = false;

    constructor(private router: Router) {
    }

    logout(): void {
      this.logout_open = false;
      this.router.navigate(['/login']);
    }
}
