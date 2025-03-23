import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface JobOffer {
  id: number;
  title: string;
  description: string;
  company: string;
  location: string;
  salary: number;
  source: string;
  contractType: string;
}

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private apiUrl = 'http://localhost:8080/offers';

  constructor(private http: HttpClient) {}

  getJobOffers(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.apiUrl);
  }

  getJobById(id: string) {
    return this.http.get<any>(`http://localhost:8080/offer/${id}`);
  }

}
