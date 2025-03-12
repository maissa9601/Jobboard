import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../service/service';

@Component({
  selector: 'app-activation',
  templateUrl: './activation.component.html',
  styleUrls: ['./activation.component.css']
})
export class ActivationComponent implements OnInit {
  message: string = 'Activation en cours...';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.authService.confirmAccount(token).subscribe({
          next: (isActivated) => {
            if (isActivated) {
              this.message = 'Compte activé avec succès ! Redirection...';
              setTimeout(() => {
                this.router.navigate(['/login']);
              }, 3000);
            } else {
              this.message = ' Lien d’activation invalide ou expiré.';
            }
          },
          error: () => {
            this.message = ' Erreur lors de l’activation du compte.';
          }
        });
      } else {
        this.message = 'Aucun token trouvé.';
      }
    });
  }
}
