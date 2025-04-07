import { Component, OnInit } from '@angular/core';
import { OfferService } from '../../../service/offer.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-job-list',
  templateUrl: './offres-list.component.html',
  imports: [FormsModule, CommonModule, RouterLink],
  styleUrls: ['./offres-list.component.css']
})
export class OffersListComponent implements OnInit {
  jobs: any[] = [];
  filteredJobs: any[] = [];

  searchKeyword: string = '';
  searchLocation: string = '';
  selectedType: string | null = null;
  constructor(private jobService: OfferService) {}

  ngOnInit(): void {
    this.jobService.getJobOffers().subscribe({
      next: (data) => {
        this.jobs = data || [];
        this.filteredJobs = [...this.jobs];
      },
      error: (err) => console.error(err)
    });
  }

  searchJobs(): void {
    this.applyFilters();
  }

  filterByType(type: string | null): void {
    if (type === null) {
      this.selectedType = null;
    } else {
      this.selectedType = this.selectedType === type ? null : type;
    }
    this.applyFilters();
  }


  applyFilters(): void {
    this.filteredJobs = this.jobs.filter(job =>
      (!this.selectedType || job.contractype?.trim().toLowerCase() === this.selectedType.trim().toLowerCase()) &&
      (this.searchKeyword === '' || job.title.trim().toLowerCase().includes(this.searchKeyword.trim().toLowerCase()) ||
        job.description.trim().toLowerCase().includes(this.searchKeyword.trim().toLowerCase())) &&
      (this.searchLocation === '' || job.location.trim().toLowerCase().includes(this.searchLocation.trim().toLowerCase()))
    );
  }

}
