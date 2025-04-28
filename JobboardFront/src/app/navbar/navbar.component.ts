import {Component, Input} from '@angular/core';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {NgClass, ViewportScroller} from '@angular/common';


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



  constructor(private viewportScroller: ViewportScroller) {}

  scrollToSection(sectionId: string) {
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

}
