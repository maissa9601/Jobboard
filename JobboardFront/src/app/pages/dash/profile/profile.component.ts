import {ChangeDetectorRef, Component, HostListener, OnInit} from '@angular/core';
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
import {Router, RouterLink} from '@angular/router';
import {DatePipe, NgClass, NgForOf, NgIf, NgOptimizedImage} from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  imports: [
    ReactiveFormsModule,
    RouterLink,
    NgForOf,
    NgIf,
    DatePipe,
    NgClass,

  ]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  photoPreviewUrl: string | ArrayBuffer | null = null;
  cvPreviewUrl: string | null = null;
  cvFileName: string = '';
  activeTab = 'profile';
  photoFile: File | null = null;
  cvFile: File | null = null;
  favorites: any[] = [];
  alertes: any[] = [];
  userId!: number;
  menuOpen = false;
  showProfileDropdown: boolean = false;

  isEditing = {
    fullName: false,
    bio: false,
    preferredLocation: false,
    preferredContractType: false,
    preferredKeywords: false
  };



  constructor(
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder,
    private profileService: CandidatService,
    private toastr: ToastrService,
    private authService: AuthService,
    private router: Router,
  ) {
    this.profileForm = this.fb.group({
      fullName: ['', Validators.required],
      bio: ['', Validators.required],
      preferredLocation: ['', Validators.required],
      preferredContractType: ['', Validators.required],
      preferredKeywords: this.fb.array([]),
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

  get preferredKeywords(): FormArray {
    return this.profileForm.get('preferredKeywords') as FormArray;
  }

  get fullNameControl(): FormControl {
    return this.profileForm.get('fullName') as FormControl;
  }

  get bioControl(): FormControl {
    return this.profileForm.get('bio') as FormControl;
  }

  get preferredLocationControl(): FormControl {
    return this.profileForm.get('preferredLocation') as FormControl;
  }

  get preferredContractTypeControl(): FormControl {
    return this.profileForm.get('preferredContractType') as FormControl;
  }

  get hasSkills(): boolean {
    return this.skills.length > 0;
  }


  get hasLanguages(): boolean {
    return this.languages.length > 0;
  }
  toggleProfileDropdown(): void {
    this.showProfileDropdown = !this.showProfileDropdown;
  }
  ngOnInit(): void {
    this.profileService.getProfile().subscribe(profile => {
      this.profileForm.patchValue({
        fullName: profile.fullName,
        bio: profile.bio,
        preferredLocation: profile.preferredLocation,
        preferredContractType: profile.preferredContractType,
        photoUrl: profile.photoUrl,
        cvUrl: profile.cvUrl
      });

      if (profile.preferredKeywords) {
        profile.preferredKeywords.forEach((keyword: string) => this.preferredKeywords.push(this.fb.control(keyword)));
      }

      if (profile.skills) {
        profile.skills.forEach((skill: string) => this.skills.push(this.fb.control(skill)));
      }

      if (profile.languages) {
        profile.languages.forEach((lang: string) => this.languages.push(this.fb.control(lang)));
      }

      this.photoPreviewUrl = profile.photoUrl ? `http://localhost:8083${profile.photoUrl}` : null;
      this.cvPreviewUrl = profile.cvUrl ? `http://localhost:8083${profile.cvUrl}` : null;
      this.cvFileName = profile.cvUrl?.split('/').pop() ?? '';

      // Désactiver tous les champs éditables par défaut
      this.fullNameControl.disable();
      this.bioControl.disable();
      this.preferredLocationControl.disable();
      this.preferredContractTypeControl.disable();
    });

    const user = this.authService.getCurrentUser();
    if (user) {
      this.userId = user.id;
      this.loadFavorites();
      this.loadAlertes();
    } else {
      console.warn('Utilisateur non connecté');
    }
  }

  toggleEdit(field: keyof typeof this.isEditing) {
    this.isEditing[field] = !this.isEditing[field];
    const control = this.profileForm.get(field);
    this.isEditing[field] ? control?.enable() : control?.disable();
  }

  loadFavorites(): void {
    this.profileService.getFavorites(this.userId).subscribe({
      next: (data: any) => this.favorites = data,
      error: err => console.error('Erreur lors du chargement des favoris :', err)
    });
  }

  loadAlertes(): void {
    this.profileService.getAlertes(this.userId).subscribe({
      next: (data: any) => this.alertes = data,
      error: err => console.error('Erreur lors du chargement des favoris :', err)
    });
  }

  removeFavorite(offerId: number): void {
    this.profileService.removeFavorite(this.userId, offerId).subscribe({
      next: () => {
        this.favorites = this.favorites.filter(fav => fav.offerId !== offerId);
      },
      error: err => console.error('Erreur lors de la suppression du favori :', err)
    });
  }

  removeAlerte(id: number): void {
    this.profileService.removeAlerte(this.userId, id).subscribe({
      next: () => {
        this.alertes = this.alertes.filter(alerte => alerte.id !== id);
      },
      error: err => console.error('Erreur lors de la suppression de lalerte:', err)
    });
  }

  marquerCommeLue(id: number): void {

    this.profileService.marquerAlerteCommeLue(id)
      .subscribe(() => {
        const alerte = this.alertes.find(a => a.id === id);
        if (alerte) {
          alerte.lu = true;
        }
      });
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

  addKeyword() {
    this.preferredKeywords.push(new FormControl('', Validators.required));
    this.cdr.detectChanges();
  }

  removeKeyword(index: number) {
    this.preferredKeywords.removeAt(index);
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
      this.cvPreviewUrl = URL.createObjectURL(file);
    }
  }

  save() {
    const formData = new FormData();
    const raw = this.profileForm.getRawValue();

    formData.append('fullName', raw.fullName);
    formData.append('bio', raw.bio);
    formData.append('preferredLocation', raw.preferredLocation);
    formData.append('preferredContractType', raw.preferredContractType);

    this.skills.value.forEach((skill: string) => formData.append('skills', skill));
    this.languages.value.forEach((lang: string) => formData.append('languages', lang));
    this.preferredKeywords.value.forEach((kw: string) => formData.append('preferredKeywords', kw));

    const uploads = [];
    if (this.photoFile) uploads.push(this.profileService.uploadPhoto(this.photoFile));
    if (this.cvFile) uploads.push(this.profileService.uploadCV(this.cvFile));

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

  isDarkMode = false;


  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.avatar-container') && !target.closest('.profile-dropdown')) {
      this.menuOpen = false;
    }
  }

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }
  goToProfile(): void {
    this.router.navigate(['/dashboard']);
    this.menuOpen = false;
  }
  navigateTo(path: string): void {
    this.menuOpen = false;
    this.router.navigate([path]);
  }
  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    document.body.classList.toggle('dark-mode', this.isDarkMode);
  }
}
