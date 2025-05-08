import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {Candidat} from '../models/candidat';

@Injectable({ providedIn: 'root' })
export class CandidatService {
  private baseUrl = 'http://localhost:8083/candidat';

  constructor(private http: HttpClient) {}



  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  getProfile(): Observable<Candidat> {
    return this.http.get<Candidat>(`${this.baseUrl}/profile/me`, this.getAuthHeaders());
  }


  updateProfile(data: any) {
    return this.http.put(`${this.baseUrl}/profile/update`, data, this.getAuthHeaders());
  }
  createProfile(data: any) {
    return this.http.post(`${this.baseUrl}/profile/create`, data, this.getAuthHeaders());
  }


  updateCvUrl(cvUrl: string) {
    return this.http.put(
      `${this.baseUrl}/profile/cv/update`,
      { cvUrl },
      this.getAuthHeaders()
    );
  }

  uploadCV(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    return this.http.post(`${this.baseUrl}/profile/cv/upload`, formData, { headers, responseType: 'text' });
  }
}
