import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../service/admin.service';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {OfferService} from '../../../service/offer.service';
import { ChartConfiguration, ChartOptions, ChartType } from 'chart.js';
import {FilterByFieldPipe} from '../../../service/filter.pipe';
import {AuthService} from '../../../service/auth.service';


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
  showAddModal = false;

  candidats: any[] = [];
  offres: any[] = [];
  admins: any[] = [];
  topViewedOffers: any[] = [];

  searchEmail = '';
  searchOffreTitre = '';
  totalAdmins: number = 0;
  totalOffers: number = 0;
  totalCandidats: number = 0;
  recentCandidats: any[] = [];



  newOffre = {
    title: '',
    contractype: '',
    description: '',
    company: '',
    location: '',
    salary: 0,
    source: ''
  };

  constructor(
    private adminService: AdminService,
    private offerService: OfferService,
    private authService: AuthService
  ) {}
  signupChartData: ChartConfiguration<'line'>['data'] = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May'],
    datasets: [
      {
        data: [10, 20, 15, 30, 25],
        label: 'Signups',
        fill: true,
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        tension: 0.4
      }
    ]
  };

  signupChartOptions: ChartOptions<'line'> = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        labels: {
          color: '#333'
        }
      }
    },
    scales: {
      x: {
        ticks: {
          color: '#333'
        }
      },
      y: {
        ticks: {
          color: '#333'
        }
      }
    }
  };
  ngOnInit(): void {
    this.loadData();
    this.getCandidats();
    this.getOffres();
    this.getAdmins();


    this.offerService.getMostViewedOffers().subscribe(data => {
      this.topViewedOffers = data;

    });


    this.authService.getStats().subscribe(stats => {
      this.totalAdmins = stats.admins;
      this.totalCandidats = stats.candidats;

    });
    this.offerService.getStats().subscribe(stats => {
     this.totalOffers = stats.offers;

    });


    this.authService.getRecentCandidats().subscribe(data => {
      this.recentCandidats = data;
    });
  }

  loadData(): void {
    this.getCandidats();
    this.getOffres();
    this.getAdmins();
    this.getTopViewedOffers();
  }

  getCandidats(): void {
    this.adminService.getCandidats().subscribe(data => {
      this.candidats = data;
    });
  }

  getOffres(): void {
    this.adminService.getOffres().subscribe(data => {
      this.offres = data;
    });
  }

  getAdmins(): void {
    this.adminService.getAdmins().subscribe(data => {
      this.admins = data;
    });
  }

  getTopViewedOffers(): void {
    this.offerService.getMostViewedOffers().subscribe(data => {
      this.topViewedOffers = data;
    });
  }

  updateCandidat(id: number): void {
    this.adminService.updateCandidat(id).subscribe(() => {
      this.getCandidats();
    });
  }

  deleteCandidat(id: number): void {
    this.adminService.deleteCandidat(id).subscribe(() => {
      this.getCandidats();
    });
  }

  deleteOffre(id: number): void {
    this.adminService.deleteOffre(id).subscribe(() => {
      this.getOffres();
    });
  }

  ajouterOffre(): void {
    this.adminService.addOffre(this.newOffre).subscribe(() => {
      this.getOffres();
      this.resetNewOffreForm();
    });
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
}
