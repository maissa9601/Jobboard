import { Injectable } from '@angular/core';
import { OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {tap, catchError, map} from 'rxjs/operators';
import { BehaviorSubject, Observable, throwError } from 'rxjs';

const authConfig: AuthConfig = {
  issuer: 'https://accounts.google.com',
  redirectUri: window.location.origin + '/login/oauth2/code/google',
  clientId: '1063839859750-8mhuftg0m4k8o3ao0u9r79b5uu9j1s0s.apps.googleusercontent.com',
  scope: 'openid profile email',
  strictDiscoveryDocumentValidation: false,
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private API_URL = 'http://localhost:8080/auth';
  //suivi de role
  private roleSubject = new BehaviorSubject<string | null>(this.getRole());
  role$ = this.roleSubject.asObservable();

  constructor(
    private oauthService: OAuthService,
    private http: HttpClient,
    private router: Router
  ) {
    this.oauthService.configure(authConfig);
    this.oauthService.loadDiscoveryDocumentAndTryLogin();
  }

  //login avec google
  googleLogin() {
    this.oauthService.initLoginFlow();
  }

  //register
  register(email: string, password: string): Observable<any> {
    const body = { email, password };
    return this.http.post(`${this.API_URL}/register`, body);
  }

  //login
  login(email: string, password: string): Observable<{ token: string; role: string }> {

    return this.http.post<{ token: string, role: string }>(`${this.API_URL}/login`, { email, password }).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        this.roleSubject.next(response.role);
        this.redirectUser(response.role);
      }),
      catchError(error => {
        console.error('❌ Erreur de connexion', error);
        return throwError(error);
      })
    );
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  private redirectUser(role: string) {
    console.log('🔍 Rôle détecté :', role);  // Debugging

    if (role === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/candidat']);
    }
  }
  forgotPassword(email: string) {
    return this.http.post(`${this.API_URL}/forgot-password`, { email });
  }
  resetPassword(token: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.API_URL}/reset-password`, { token, newPassword });
  }

 //connecté?
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  //confirmation
  confirmAccount(token: string): Observable<boolean> {
    return this.http.get<{ activated: boolean }>(`${this.API_URL}/confirm?token=${token}`).pipe(
      tap(response => {
        if (response.activated) {
          console.log('compte activé avec succès !');
        } else {
          console.error('activation échouée.');
        }
      }),
      map(response => response.activated),
      catchError(error => {
        console.error('erreur lors de la confirmation du compte', error);
        return throwError(() => error);
      })
    );
  }
  //dcnx
  logout(): void {
    localStorage.clear();
    this.roleSubject.next(null);
    this.router.navigate(['/login']);
  }
}
