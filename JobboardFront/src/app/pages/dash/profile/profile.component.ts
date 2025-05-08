import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { CandidatService } from '../../../service/candidat.service';
import {RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  imports: [
    ReactiveFormsModule,
    RouterLink,
    NgForOf,
    NgIf
  ]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  photoPreviewUrl: string | ArrayBuffer | null = null;
  activeTab = 'dashboard';
  isEditing = { fullName: false, bio: false };


  constructor(private fb: FormBuilder, private profileService: CandidatService, private toastr: ToastrService) {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required],
      bio: ['', Validators.required],
      photo: [null],
      skills: this.fb.array([]),
      languages: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.profileService.getProfile().subscribe(profile => {
      console.log('Profile reçu:', profile);
      this.profileForm.patchValue({
        fullName: profile.fullName,
        bio: profile.bio
      });

      // Initialiser skills et languages
      if (profile.skills) {
        profile.skills.forEach((skill: string) => this.skills.push(this.fb.control(skill)));
      }
      if (profile.languages) {
        profile.languages.forEach((lang: string) => this.languages.push(this.fb.control(lang)));
      }

      this.profileForm.get('fullName')?.disable();
      this.profileForm.get('bio')?.disable();
      //this.alerts = profile.alerts || [];
    });
  }

  get skills() {
    return this.profileForm.get('skills') as FormArray;
  }

  get languages() {
    return this.profileForm.get('languages') as FormArray;
  }

  toggleEdit(field: 'fullName' | 'bio') {
    this.isEditing[field] = !this.isEditing[field];
    const control = this.profileForm.get(field);
    if (this.isEditing[field]) {
      control?.enable();
    } else {
      control?.disable();
    }
  }

  addSkill() {
    this.skills.push(this.fb.control('', Validators.required));
  }

  removeSkill(i: number) {
    this.skills.removeAt(i);
  }

  addLanguage() {
    this.languages.push(this.fb.control('', Validators.required));
  }

  removeLanguage(i: number) {
    this.languages.removeAt(i);
  }

  onPhotoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.profileForm.patchValue({ photo: file });

      const reader = new FileReader();
      reader.onload = () => {
        this.photoPreviewUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }
  cvFileName: string = '';
  cvFile: File | null = null;

  onCvSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.cvFileName = file.name;
      this.cvFile = file;
    }
  }


  save() {
    const formData = new FormData();
    formData.append('fullName', this.profileForm.getRawValue().fullName);
    formData.append('bio', this.profileForm.getRawValue().bio);

    if (this.profileForm.value.photo) {
      formData.append('photo', this.profileForm.value.photo);
    }
    if (this.cvFile) {
      formData.append('cv', this.cvFile);
    }


    this.skills.value.forEach((skill: string) => {
      formData.append('skills', skill);
    });

    this.languages.value.forEach((lang: string) => {
      formData.append('languages', lang);
    });

    this.profileService.updateProfile(formData).subscribe({
      next: () => {
      this.toastr.success('Profile updated successfully');},
      error: (err: any) => {
        this.toastr.error('Error while updating profile');
        console.error(err);

      }
    });
  }


  logout() {
    localStorage.clear();
    window.location.href = '/login';
  }


  asFormControl(control: AbstractControl): FormControl {
    return control as FormControl;
  }

}
