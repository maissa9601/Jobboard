import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import {AuthService} from '../../../service/service';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.authService.login(email, password).subscribe({
        next: (response) => {
          console.log( response);

        },
        error: (err) => {
          console.error(err);
          this.errorMessage = 'email ou mot de passe incorrect.';
        }
      });
    } else {
      this.errorMessage = 'Veuillez remplir correctement le formulaire.';
    }
  }
  sendResetEmail() {
    const email = prompt("Veuillez entrer votre email pour la réinitialisation :");

    if (!email) return;

    this.authService.forgotPassword(email).subscribe({
      next: () => {
        alert("un mail de reset a été envoyé à votre mail.");
      },
      error: (err) => {
        console.error(err);
        alert("Erreur lors de l'envoi de l'email.");
      }
    });
  }
}
