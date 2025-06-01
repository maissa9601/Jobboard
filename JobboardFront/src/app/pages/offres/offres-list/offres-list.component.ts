import { Component, OnInit } from '@angular/core';
import { OfferService } from '../../../service/offer.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {Router} from "@angular/router";
import {CandidatService} from '../../../service/candidat.service';


@Component({
  selector: 'app-job-list',
  templateUrl: './offres-list.component.html',
  imports: [FormsModule, CommonModule],
  styleUrls: ['./offres-list.component.css']
})
export class OffersListComponent implements OnInit {
  jobs: any[] = [];
  filteredJobs: any[] = [];
  searchKeyword: string = '';
  searchLocation: string = '';
  selectedType: string | null = null;
  selectedExperience: string | null = null;
  selectedSource: string | null = null;
  minSalary: number | null = null;
  sortOrder: string = 'latest';
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalPages: number = 1;
  contractTypes: string[] = [];
  experiences: string[] = [];
  sources: string[] = [];

  constructor(private jobService: OfferService,private router :Router,private candidatService : CandidatService) { }


  ngOnInit(): void {
    this.jobService.getJobOffers().subscribe({
      next: (data) => {
        this.jobs = data || [];
        this.filteredJobs = [...this.jobs];

        // Extraire dynamiquement les filtres
        this.contractTypes = [...new Set(this.jobs.map(j => j.contractype).filter(Boolean))];
        this.experiences = [...new Set(this.jobs.map(j => j.experience).filter(Boolean))];
        this.sources = [...new Set(this.jobs.map(j => j.source).filter(Boolean))];
      },
      error: (err) => console.error(err)
    });
  }

  resetFilters(): void {
    this.searchKeyword = '';
    this.searchLocation = '';
    this.selectedType = '';
    this.selectedExperience = '';
    this.selectedSource = '';
    this.minSalary = null;
    this.sortOrder = '';
    this.sortOrder = 'latest';
    this.currentPage = 1;
    this.applyFilters();
  }

  applyFilters(): void {
    let filtered = this.jobs.filter(job =>
      (!this.selectedType || job.contractype?.trim().toLowerCase() === this.selectedType.trim().toLowerCase()) &&
      (!this.searchKeyword || job.title?.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
        job.description?.toLowerCase().includes(this.searchKeyword.toLowerCase())) &&
      (!this.searchLocation || job.location?.toLowerCase().includes(this.searchLocation.toLowerCase())) &&
      (!this.selectedExperience || job.experience === this.selectedExperience) &&
      (!this.selectedSource || job.source === this.selectedSource) &&
      (!this.minSalary || job.salary >= this.minSalary)
    );

    switch (this.sortOrder) {
      case 'latest':
        filtered.sort((a, b) => new Date(b.published).getTime() - new Date(a.published).getTime());
        break;
      case 'oldest':
        filtered.sort((a, b) => new Date(a.published).getTime() - new Date(b.published).getTime());
        break;
      case 'salary':
        filtered.sort((a, b) => b.salary - a.salary);
        break;
    }

    this.totalPages = Math.ceil(filtered.length / this.itemsPerPage);
    if (this.currentPage > this.totalPages) this.currentPage = 1;

    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.filteredJobs = filtered.slice(startIndex, endIndex);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.applyFilters();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.applyFilters();
    }
  }
  seeMore(offreId: number) {
    this.jobService.incrementViews(offreId).subscribe(offre => {
      this.router.navigate(['/offer',offreId]);
    });
  }


}
