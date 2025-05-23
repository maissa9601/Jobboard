import {Component, HostListener} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {NgIf, NgOptimizedImage} from '@angular/common';



@Component({
  selector: 'app-navbar',

  templateUrl: './navbar.component.html',
  imports: [
    RouterLink
  ],
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {


  activeLink: string = 'home';
  isLoggedIn = false;
  userName = '';
  menuOpen = false;
  photoPreviewUrl: string | ArrayBuffer | null = null;

  scrollToSection(sectionId: string) {
    this.activeLink = sectionId;
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  }
  constructor(public router: Router) {}

  getNavbarClass(): string {
    if (this.router.url.startsWith('/offers')) {
      return 'navbar white-navbar';
    } else {
      return 'navbar default-navbar';
    }
  }
  ngOnInit(): void {
    const token = localStorage.getItem('token');
    this.isLoggedIn = !!token;

    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      this.userName = payload.email || 'User';
    }
  }



  goToProfile(): void {
    this.router.navigate(['/dashboard']);
    this.menuOpen = false;
  }
  navigateTo(path: string): void {
    this.menuOpen = false;
    this.router.navigate([path]);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.isLoggedIn = false;
    this.menuOpen = false;
    this.router.navigate(['/home']);
  }



  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.avatar-container') && !target.closest('.profile-dropdown')) {
      this.menuOpen = false;
    }
  }
  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }
}
