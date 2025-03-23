import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

import {AuthService} from '../../../service/auth.service';
import {NgIf} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;


  constructor(private fb: FormBuilder, private authService: AuthService, private toastr: ToastrService) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.toastr.error('Fill the form correctly.', 'Error');
      return;
    }

    const { email, password } = this.loginForm.value;
    this.authService.login(email, password).subscribe({
      next: (response) => {
        this.toastr.success('Login successful!', 'Success');

      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Incorrect email or password.', 'Error');
      }
    });
  }
  public hasError(field: string, error: string): boolean {
    return this.f[field].touched && this.f[field].hasError(error);
  }


  sendResetEmail() {
    Swal.fire({
      title: 'Reset Password',
      input: 'email',
      inputLabel: 'Enter your email',
      inputPlaceholder: 'example@gmail.com',
      showCancelButton: true,
      confirmButtonText: 'Send',
      preConfirm: (email) => {
        if (!email) {
          Swal.showValidationMessage('Email is required');
        }
        return email;
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        this.authService.forgotPassword(result.value).subscribe({
          next: () => {
            this.toastr.success('A reset email has been sent!', 'Success');
          },
          error: (err) => {
            console.error(err);
            this.toastr.error('Error while sending reset email.', 'Error');
          }
        });
      }
    });
  }


  get f() {
    return this.loginForm.controls;
  }
}
