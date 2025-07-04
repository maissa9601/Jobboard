import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../../../service/auth.service';
import {NgIf} from '@angular/common';
import {ToastrService} from 'ngx-toastr';
import Swal from 'sweetalert2';
import {RouterLink} from '@angular/router';


@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, NgIf, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  isLoading:boolean = false;


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
    this.isLoading=true;

    const { email, password } = this.loginForm.value;
    this.authService.login(email, password).subscribe({
      next: () => {
        this.toastr.success('Login successful!', 'Success');
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Incorrect email or password.', 'Error');
        this.isLoading = false;
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
