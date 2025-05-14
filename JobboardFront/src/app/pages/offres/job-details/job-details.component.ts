import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {OfferService} from '../../../service/offer.service';
import {AuthService} from '../../../service/auth.service';
import {CandidatService} from '../../../service/candidat.service';
import {ToastrService} from 'ngx-toastr';

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
    private jobService: OfferService,private authService: AuthService ,private router: Router,private candidatService: CandidatService,private toastr: ToastrService
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

  saveToFavorites(offer: any) {
    const user = this.authService.getCurrentUser();

    if (!user || user.role !== 'CANDIDAT') {

      this.router.navigate(['/signup']);
      return;
    }

    const favori = {
      userId: user.id,
      offerId: offer.id,
      title: offer.title,
      offerUrl: offer.url
    };

    this.candidatService.addToFavorites(favori).subscribe({
      next: () => this.toastr.success('Offer saved to favorites!'),
      error: () => this.toastr.error('Failed to save offer.')
    });
  }
}

