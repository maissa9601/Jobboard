import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import {CandidatService} from '../../../service/candidat.service';
import {MatIcon} from "@angular/material/icon";
import {ToastrService} from 'ngx-toastr';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-complete-profile',
  templateUrl: './complete-profile.component.html',
  imports: [
    ReactiveFormsModule,
    MatIcon,

  ],
  styleUrls: ['./complete-profile.component.css']
})
export class CompleteProfileComponent {
  completeForm: FormGroup;


  constructor(
    private fb: FormBuilder,
    private candidatService: CandidatService,
    private router: Router,
    private toastr: ToastrService,
  ) {
    this.completeForm = this.fb.group({
      fullName: ['', Validators.required],
      bio: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.completeForm.invalid) return;

    const {fullName, bio} = this.completeForm.value;
    this.candidatService.createProfile({fullName, bio}).subscribe({
      next: () => {
        this.toastr.info('Profile created successfully', 'Info');
        setTimeout(() => {
          this.router.navigate(['/dashboard']);
        }, 3500);
      },
      error: err => {
        console.error(err);
        this.toastr.error('Error while creating profile', 'Error');
      }
    });
  }
}
