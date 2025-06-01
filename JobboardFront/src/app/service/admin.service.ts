
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Admin } from '../models/Admin';
import {JobOffer} from './offer.service';
import {Reclamation} from '../models/Reclamation';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = 'http://localhost:8082/admin';

  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  // CRUD Candidats
  getCandidats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/candidats`, this.getAuthHeaders());
  }

  updateCandidat(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/candidats/${id}/promote`, {}, this.getAuthHeaders());
  }

  deleteCandidat(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/candidats/${id}`, this.getAuthHeaders());
  }

  // CRUD Offres
  getOffres(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/offers`, this.getAuthHeaders());
  }

  addOffre(offre: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/offer/add`, offre, this.getAuthHeaders());
  }

  deleteOffre(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/offers/${id}`, this.getAuthHeaders());
  }

  // Admins ne9sa promote to admin
  getAdmins(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/admins`, this.getAuthHeaders());
  }
//stats de users
  getStats(): Observable<any> {
 return this.http.get(`${this.baseUrl}/stats`, this.getAuthHeaders());
}

getRecentCandidats(): Observable<any[]> {
 return this.http.get<any[]>(`${this.baseUrl}/candidats/recent`, this.getAuthHeaders());
}

  // Admin Profile
  getProfile(): Observable<Admin> {
    return this.http.get<Admin>(`${this.baseUrl}/profile/me`, this.getAuthHeaders());
  }

  createProfile(): Observable<Admin> {
    return this.http.post<Admin>(`${this.baseUrl}/profile/create`, {}, this.getAuthHeaders());
  }



  updateProfile(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(
      `${this.baseUrl}/profile/photo/upload`,
      formData,
      {
        ...this.getAuthHeaders(),
        responseType: 'text'
      }
    );
  }
  //stats de offers
  getMostViewedOffers(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(`${this.baseUrl}/offers/most-viewed`, this.getAuthHeaders());
  }

  getStat(): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats`, this.getAuthHeaders());
  }
  //reclamations
  getReclamations(): Observable<Reclamation[]> {
    return this.http.get<Reclamation[]>(`${this.baseUrl}/reclamations`, this.getAuthHeaders());
  }
  deleteReclamation(id: number) {
    return this.http.delete(`${this.baseUrl}/delete/${id}`);
  }



}
