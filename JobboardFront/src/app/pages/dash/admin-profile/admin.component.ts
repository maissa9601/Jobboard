import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgChartsModule } from 'ng2-charts';
import { AdminService } from '../../../service/admin.service';
import { AuthService } from '../../../service/auth.service';
import { FilterByFieldPipe } from '../../../service/filter.pipe';
import { Admin } from '../../../models/Admin';
import {ChartConfiguration, ChartOptions} from 'chart.js';
import {Reclamation} from '../../../models/Reclamation';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, NgChartsModule, FilterByFieldPipe],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  activeTab = 'dashboard';
  active = 'admin';
  isDarkMode = false;
  showAddModal = false;
  showProfileDropdown: boolean = false;
  candidats: any[] = [];
  offres: any[] = [];
  admins: any[] = [];
  topViewedOffers: any[] = [];
  recentCandidats: any[] = [];
  totalAdmins = 0;
  totalOffers = 0;
  totalCandidats = 0;
  searchEmail = '';
  searchOffreTitre = '';
  admin: Admin = { email: '', photoUrl: '' };
  photoFile: File | null = null;
  photoPreviewUrl: string | null | undefined;
  reclamations: Reclamation[] = [];
  newOffre = {
    title: '',
    contractype: '',
    description: '',
    company: '',
    location: '',
    salary: 0,
    source: ''
  };

  signupChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
    datasets: [{
      data: [10, 20, 15, 30, 25],
      label: 'Signups',
      fill: true,
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.2)',
      tension: 0.4
    }]
  };

  signupChartOptions: ChartOptions<'line'> = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        labels: { color: '#333' }
      }
    },
    scales: {
      x: { ticks: { color: '#333' } },
      y: { ticks: { color: '#333' } }
    }
  };

  constructor(
    private adminService: AdminService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadData();
    this.checkOrCreateAdminProfile();

    this.adminService.getStats().subscribe(stats => {
      this.totalAdmins = stats.admins;
      this.totalCandidats = stats.candidats;
    });

    this.adminService.getStat().subscribe(stats => {
      this.totalOffers = stats.offers;
    });

    this.adminService.getRecentCandidats().subscribe(data => {
      this.recentCandidats = data;
    });

    this.adminService.getReclamations().subscribe({
      next: (data) => {
        this.reclamations = data;
      },
      error: (err) => {
        console.error("Erreur lors du chargement des réclamations :", err);
      }
    });
  }




  toggleDarkMode(): void {
    this.isDarkMode = !this.isDarkMode;
    document.body.classList.toggle('dark-mode', this.isDarkMode);
  }
  toggleProfileDropdown(): void {
    this.showProfileDropdown = !this.showProfileDropdown;
  }

  // Chargement des données principales
  loadData(): void {
    this.getCandidats();
    this.getOffres();
    this.getAdmins();
    this.getTopViewedOffers();
  }

  // ===== Admin Profile =====
  checkOrCreateAdminProfile(): void {
    this.adminService.getProfile().subscribe({
      next: profile => this.setAdminProfile(profile),
      error: err => {
        if (err.status === 404) {
          this.adminService.createProfile().subscribe({
            next: profile => this.setAdminProfile(profile),
            error: () => alert("Erreur lors de la création du profil administrateur.")
          });
        } else {
          console.error('Erreur lors de la récupération du profil admin :', err);
        }
      }
    });
  }

  private setAdminProfile(admin: Admin): void {
    this.admin = admin;
    const baseUrl = 'http://localhost:8082';
    this.photoPreviewUrl = admin.photoUrl
      ? admin.photoUrl.startsWith('http')
        ? admin.photoUrl
        : `${baseUrl}${admin.photoUrl}`
      : '/default-avatar.png';
  }


  getAdminProfile(): void {
    this.adminService.getProfile().subscribe(admin => this.setAdminProfile(admin));
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.photoFile = input.files[0];

      // Pour aperçu direct
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.photoPreviewUrl = e.target.result;
      };
      reader.readAsDataURL(this.photoFile);


      this.updateAdminProfile();
    }
  }
  logout(): void {
    this.authService.logout();

  }


  updateAdminProfile(): void {
    if (this.photoFile) {
      this.adminService.updateProfile(this.photoFile).subscribe({
        next: () => {
          alert('Photo mise à jour avec succès.');
          this.getAdminProfile();
        },
        error: () => alert('Erreur lors de la mise à jour de la photo.')
      });
    }
  }

  // ===== Offres =====
  getOffres(): void {
    this.adminService.getOffres().subscribe(data => this.offres = data);
  }

  ajouterOffre(): void {
    this.adminService.addOffre(this.newOffre).subscribe(() => {
      this.getOffres();
      this.resetNewOffreForm();
    });
  }

  deleteOffre(id: number): void {
    this.adminService.deleteOffre(id).subscribe(() => this.getOffres());
  }

  private resetNewOffreForm(): void {
    this.newOffre = {
      title: '',
      contractype: '',
      description: '',
      company: '',
      location: '',
      salary: 0,
      source: ''
    };
  }

  getTopViewedOffers(): void {
    this.adminService.getMostViewedOffers().subscribe(data => {
      this.topViewedOffers = data;
    });
  }

  // ===== Candidats =====
  getCandidats(): void {
    this.adminService.getCandidats().subscribe(data => this.candidats = data);
  }

  updateCandidat(id: number): void {
    this.adminService.updateCandidat(id).subscribe(() => this.getCandidats());
  }

  deleteCandidat(id: number): void {
    this.adminService.deleteCandidat(id).subscribe(() => this.getCandidats());
  }



  // ===== Admins =====
  getAdmins(): void {
    this.adminService.getAdmins().subscribe(data => this.admins = data);
  }


  deleteReclamation(id: number): void {

      this.adminService.deleteReclamation(id).subscribe({
        next: () => {
          this.reclamations = this.reclamations.filter(r => r.id !== id);
        },
        error: (err) => {
          console.error(err);
        }
      });

  }
}
