import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {HousingUnit} from '../model/data';

@Injectable({
  providedIn: 'root'
})
export class HousingUnitService {
  private apiUrl = 'http://localhost:8080/api/housing-units';

  constructor(private http: HttpClient) {
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getHousingUnits(): Observable<HousingUnit[]> {
    return this.http.get<HousingUnit[]>(this.apiUrl, {headers: this.getHeaders()});
  }

  getHousingUnitById(id: number): Observable<HousingUnit> {
    return this.http.get<HousingUnit>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
  }

  createHousingUnit(housingUnit: HousingUnit): Observable<HousingUnit> {
    return this.http.post<HousingUnit>(this.apiUrl, housingUnit, {headers: this.getHeaders()});
  }

  updateHousingUnit(id: number, housingUnit: HousingUnit): Observable<HousingUnit> {
    return this.http.put<HousingUnit>(`${this.apiUrl}/${id}`, housingUnit, {headers: this.getHeaders()});
  }

  deleteHousingUnit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
  }
}
