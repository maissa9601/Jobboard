import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {OfferService} from '../../service/offer.service';
import {NgForOf, SlicePipe} from '@angular/common';

@Component({
  selector: 'app-home',

  templateUrl: './home.component.html',
  imports: [
    RouterLink,
    SlicePipe,
    NgForOf
  ],
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  latestOffers: any[] = [];

  constructor(private offerService: OfferService, private router: Router) {}

  ngOnInit(): void {
    this.offerService.getJobOffers().subscribe({
      next: (data) => {

        this.latestOffers = data.slice(0, 3);
      },
      error: (err) => console.error(err)
    });

  }


}
