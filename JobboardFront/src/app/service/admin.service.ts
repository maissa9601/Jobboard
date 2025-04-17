import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = 'http://localhost:8080/admin';

  constructor(private http: HttpClient) {}

  updateCandidat(id: number) {
    return this.http.post(`${this.baseUrl}/candidats/${id}`, null); // null car pas de body
  }

  deleteCandidat(id: number) {
    return this.http.delete(`${this.baseUrl}/candidats/${id}/prormote`);
  }

  getCandidats() {
    return this.http.get<any[]>(`${this.baseUrl}/candidats`);
  }

  getOffres() {
    return this.http.get<any[]>(`${this.baseUrl}/offers`);
  }

  addOffre(offre: any) {
    return this.http.post(`${this.baseUrl}/offer`, offre);
  }

  deleteOffre(id: number) {
    return this.http.delete(`${this.baseUrl}/offers/${id}`);
  }

  getAdmins() {
    return this.http.get<any[]>(`${this.baseUrl}/admins`);

  }
}
