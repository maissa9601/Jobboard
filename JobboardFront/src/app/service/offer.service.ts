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
  url:string;
  contractType: string;
}

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  private apiUrl = 'http://localhost:8081/';

  constructor(private http: HttpClient) {}

  getJobOffers(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.apiUrl + 'offers');
  }

  getJobById(id: string) {
    return this.http.get<any>(`${this.apiUrl}offer/${id}`);
  }

}
