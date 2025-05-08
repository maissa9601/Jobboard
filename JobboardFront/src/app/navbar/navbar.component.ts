import {Component} from '@angular/core';
import { RouterLink, RouterLinkActive} from '@angular/router';



@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {


  activeLink: string = 'home';

  scrollToSection(sectionId: string) {
    this.activeLink = sectionId;
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }



}
