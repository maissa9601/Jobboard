import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {OfferService} from '../../../service/offer.service';

@Component({
  selector: 'app-job-details',
  imports: [
    RouterLink
  ],
  templateUrl: './job-details.component.html',
  styleUrls: ['./job-details.component.css']
})

export class JobDetailsComponent implements OnInit {
  job: any;

  constructor(
    private route: ActivatedRoute,
    private jobService: OfferService
  ) {}

  ngOnInit(): void {
    const jobId = this.route.snapshot.paramMap.get('id');
    if (jobId) {
      this.jobService.getJobById(jobId).subscribe({
        next: (data) => this.job = data,
        error: (err) => console.error(err)
      });
    }
  }
}

