import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {HoustingUnit} from '../model/data';

@Injectable({
  providedIn: 'root'
})
export class HoustingUnitService {
  private apiUrl = 'http://localhost:8080/api/housing-units';

  constructor(private http: HttpClient) {
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getHousingUnits(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, {headers: this.getHeaders()});
  }

  getHousingUnitById(id: number): Observable<HoustingUnit> {
    return this.http.get<HoustingUnit>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
  }

  createHousingUnit(housingUnit: HoustingUnit): Observable<HoustingUnit> {
    return this.http.post<HoustingUnit>(this.apiUrl, housingUnit, {headers: this.getHeaders()});
  }

  updateHousingUnit(id: number, housingUnit: HoustingUnit): Observable<HoustingUnit> {
    return this.http.put<HoustingUnit>(`${this.apiUrl}/${id}`, housingUnit, {headers: this.getHeaders()});
  }

  deleteHousingUnit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
  }
}
