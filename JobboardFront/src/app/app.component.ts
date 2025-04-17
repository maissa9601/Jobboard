import { Component } from '@angular/core';
import {NavigationEnd, RouterOutlet} from '@angular/router';
import { Router } from '@angular/router';
import {ReactiveFormsModule} from '@angular/forms';
import {ToastrModule} from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import {NavbarComponent} from './navbar/navbar.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,CommonModule,
    ReactiveFormsModule, ToastrModule, NavbarComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'JobboardFront';
  constructor(private router: Router) {}
shouldShowNavbar(): boolean {
  const excludedRoutes = ['/login', '/signup', '/admin','/reset'];
  return !excludedRoutes.some(route => this.router.url.includes(route));
}




}
