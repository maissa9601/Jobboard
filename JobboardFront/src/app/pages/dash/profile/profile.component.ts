import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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
import { RouterLink } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import {forkJoin, of} from 'rxjs';
import {AuthService} from '../../../service/auth.service';
import {OfferService} from '../../../service/offer.service';

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
  cvPreviewUrl: string | null = null;
  cvFileName: string = '';
  activeTab = 'profile';
  isEditing = { fullName: false, bio: false };
  photoFile: File | null = null;
  cvFile: File | null = null;
  favorites: any[] = [];
  userId!: number;

  constructor(
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
    private profileService: CandidatService,
    private toastr: ToastrService,
    private authService: AuthService,

  ) {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required],
      bio: ['', Validators.required],
      photoUrl: [''],
      cvUrl: [''],
      skills: this.fb.array([]),
      languages: this.fb.array([])
    });
  }

  get skills(): FormArray {
    return this.profileForm.get('skills') as FormArray;
  }

  get languages(): FormArray {
    return this.profileForm.get('languages') as FormArray;
  }

  get fullNameControl(): FormControl {
    return this.profileForm.get('fullName') as FormControl;
  }

  get bioControl(): FormControl {
    return this.profileForm.get('bio') as FormControl;
  }

  get hasSkills(): boolean {
    return this.skills.length > 0;
  }

  get hasLanguages(): boolean {
    return this.languages.length > 0;
  }

  ngOnInit(): void {
    this.profileService.getProfile().subscribe(profile => {
      this.profileForm.patchValue({
        fullName: profile.fullName,
        bio: profile.bio,
        photoUrl: profile.photoUrl,
        cvUrl: profile.cvUrl
      });

      this.photoPreviewUrl = profile.photoUrl ? `http://localhost:8083${profile.photoUrl}` : null;

      this.cvPreviewUrl = profile.cvUrl ? `http://localhost:8083${profile.cvUrl}` : null;

      this.cvFileName = profile.cvUrl?.split('/').pop() ?? '';

      if (profile.skills) {
        profile.skills.forEach((skill: string) => this.skills.push(this.fb.control(skill)));
      }
      if (profile.languages) {
        profile.languages.forEach((lang: string) => this.languages.push(this.fb.control(lang)));
      }

      this.fullNameControl.disable();
      this.bioControl.disable();
    });
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userId = user.id;
      this.loadFavorites();
    } else {

      console.warn('Utilisateur non connecté');
    }
  }

  loadFavorites(): void {
    this.profileService.getFavorites(this.userId).subscribe({
      next: (data: any) => {
        this.favorites = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des favoris :', err);
      }
    });
  }
  removeFavorite(offerId: number): void {
    this.profileService.removeFavorite(this.userId, offerId).subscribe({
      next: () => {
        // Supprimer de la liste locale
        this.favorites = this.favorites.filter(fav => fav.offerId !== offerId);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression du favori :', err);
      }
    });
  }

  toggleEdit(field: 'fullName' | 'bio') {
    this.isEditing[field] = !this.isEditing[field];
    const control = this.profileForm.get(field);
    this.isEditing[field] ? control?.enable() : control?.disable();
  }

  addSkill() {
    this.skills.push(new FormControl('', Validators.required));
    this.cdr.detectChanges();
  }

  removeSkill(index: number) {
    this.skills.removeAt(index);
    this.cdr.detectChanges();
  }

  addLanguage() {
    this.languages.push(new FormControl('', Validators.required));
    this.cdr.detectChanges();
  }

  removeLanguage(index: number) {
    this.languages.removeAt(index);
    this.cdr.detectChanges();
  }

  onPhotoSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.photoFile = file;
      const reader = new FileReader();
      reader.onload = () => {
        this.photoPreviewUrl = reader.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onCvSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.cvFile = file;
      this.cvFileName = file.name;
      this.cvPreviewUrl = URL.createObjectURL(file); // preview for download
    }
  }

  save() {
    const formData = new FormData();
    formData.append('fullName', this.profileForm.getRawValue().fullName);
    formData.append('bio', this.profileForm.getRawValue().bio);

    this.skills.value.forEach((skill: string) => formData.append('skills', skill));
    this.languages.value.forEach((lang: string) => formData.append('languages', lang));

    const uploads = [];

    if (this.photoFile) {
      uploads.push(this.profileService.uploadPhoto(this.photoFile));
    }
    if (this.cvFile) {
      uploads.push(this.profileService.uploadCV(this.cvFile));
    }

    forkJoin(uploads.length > 0 ? uploads : [of(null)]).subscribe({
      next: () => {
        this.profileService.updateProfile(formData).subscribe({
          next: () => this.toastr.success('Profile updated successfully'),
          error: err => {
            this.toastr.error('Error while updating profile');
            console.error(err);
          }
        });
      },
      error: err => {
        this.toastr.error('Error uploading files');
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
