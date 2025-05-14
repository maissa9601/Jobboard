import {Component, OnInit} from '@angular/core';
import {CandidatService} from '../../../service/candidat.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-candidat',
  imports: [],
  templateUrl: './candidat.component.html',
  styleUrl: './candidat.component.css'
})
export class CandidatComponent implements OnInit {


  constructor(private candidatService: CandidatService, private router: Router) {}

  ngOnInit(): void {
    this.candidatService.getProfile().subscribe({
      next: (profile) => {
        if (profile.fullName && profile.bio) {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/complete']);
        }
      },
      error: () => {
        this.router.navigate(['/complete']);
      }
    });
  }

}

