import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {AuthService} from '../../../service/auth.service';

@Component({
  selector: 'app-auth-callback',
  template: `<p>Connexion en cours...</p>`
})
export class AuthCallbackComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];

      console.log("🔹 Token received:", token);

      if (token) {
        this.authService.storeToken(token);
        this.router.navigate(['/candidat']);
      }
      else{
        this.router.navigate(['/signup']);
      }
    });
  }
}
