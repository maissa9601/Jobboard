import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CommonModule,

  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  signupForm: FormGroup;
  isLoading: boolean = false;


  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private toastr: ToastrService) {
    this.signupForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });


  }

  // Vérification si les mots de passe correspondent
  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { mismatch: true };
  }

  // Connexion avec Google
  signInWithGoogle() {
    this.authService.googleLogin();
  }
  public hasError(field: string, error: string): boolean {
    return this.f[field].touched && this.f[field].hasError(error);
  }


  // Soumission du formulaire
  onSubmit(): void {
    if (this.signupForm.invalid) {
      this.toastr.error('Form invalid', 'Error');
      return;
    }

    this.isLoading = true;

    const { email, password } = this.signupForm.value;
    this.authService.register(email, password).subscribe({
      next: () => {
        this.toastr.info('A confirmation email has been sent. Please check your inbox.', 'Info');
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Error while registering.', 'Error');
        this.isLoading = false;
        console.error(err);
      },
    });
  }


  // controle de formulaire
  public get f() {
    return this.signupForm.controls;
  }


}
