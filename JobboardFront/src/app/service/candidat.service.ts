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

  //gestion profile candidat
  getProfile(): Observable<Candidat> {
    return this.http.get<Candidat>(`${this.baseUrl}/profile/me`, this.getAuthHeaders());
  }
  updateProfile(data: any) {
    return this.http.put(`${this.baseUrl}/profile/update`, data, this.getAuthHeaders());
  }
  createProfile(data: any) {
    return this.http.post(`${this.baseUrl}/profile/create`, data, this.getAuthHeaders());
  }

  uploadPhoto(file: File) {
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

  uploadCV(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(
      `${this.baseUrl}/profile/cv/upload`,
      formData,
      {
        ...this.getAuthHeaders(),
        responseType: 'text' // Same here
      }
    );
  }
//gestion favorits
  addToFavorites(favori: {
    userId: number;
    offerId: number;
    title: string;
    offerUrl: string;
  }) {
    return this.http.post(`${this.baseUrl }/favorite/add`, favori,this.getAuthHeaders());
  }

  getFavorites(userId: number) {
    return this.http.get(`${this.baseUrl }/favorite/${userId}`,this.getAuthHeaders());
  }

  removeFavorite(userId: number, offerId: number) {
    return this.http.delete(`${this.baseUrl }/favorite/${userId}/${offerId}`,this.getAuthHeaders());
  }
  //gestion d'alerts
  removeAlerte(userId: number, id: number) {
    return this.http.delete(`${this.baseUrl}/alertes/${userId}/${id}`, this.getAuthHeaders());
  }
  getAlertes(userId: number) {
    return this.http.get(`${this.baseUrl }/alertes/${userId}`,this.getAuthHeaders());
  }
  marquerAlerteCommeLue(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/alertes/${id}/lu`, {}, this.getAuthHeaders());
  }


}
