import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {AuthService} from '../../../service/auth.service';
import {ToastrService} from 'ngx-toastr';


@Component({
  selector: 'app-reset',
  standalone: true,
  templateUrl: './reset.component.html',
  styleUrls: ['./reset.component.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule
  ]
})
export class ResetComponent {
  resetForm: FormGroup;
  token: string = '';
  submitted: boolean = false;
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService,
  ) {
    this.resetForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });


  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password');
    const confirmPassword = group.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ mismatch: true });
      return { mismatch: true };
    }
    return null;
  }
  public hasError(field: string, error: string): boolean {
    return this.f[field].touched && this.f[field].hasError(error);
  }
  onSubmit() {
    this.submitted = true; //active les erreurs

    if (this.resetForm.invalid) {

      return;
    }
    this.isLoading =true;

    const password = this.resetForm.value.password;

    this.authService.resetPassword(this.token, password).subscribe({
      next: () => {
        this.toastr.success('Your password has been changed successfully', 'Success');
        this.isLoading =false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error("Erreur lors de la réinitialisation :", err);
        this.toastr.error("Error while reseting");
        this.isLoading = false;}

    });
  }
  // controle de formulaire
  get f() {
    return this.resetForm.controls;
  }
}
