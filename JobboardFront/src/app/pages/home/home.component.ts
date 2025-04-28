import {Component, OnInit} from '@angular/core';
import {RouterLink} from '@angular/router';
import {NgForOf, SlicePipe} from '@angular/common';
import {OfferService} from '../../service/offer.service';

@Component({
  selector: 'app-home',
  imports: [
    RouterLink,
    SlicePipe,
    NgForOf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  latestOffers: any[] = [];

  constructor(private offerService: OfferService) {}

  ngOnInit(): void {
    this.offerService.getJobOffers().subscribe({
      next: (data) => {

        this.latestOffers = data.slice(0, 3);
      },
      error: (err) => console.error(err)
    });
  }
}
