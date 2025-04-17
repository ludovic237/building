import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable, of } from 'rxjs';
    import { HousingUnit } from '../model/data';

    @Injectable({
      providedIn: 'root'
    })
    export class HousingUnitService {
      private apiUrl = '/api/housing-units';

      constructor(private http: HttpClient) {}

      getHousingUnits(): Observable<HousingUnit[]> {
        // return this.http.get<HousingUnit[]>(this.apiUrl);
        const mockData: HousingUnit[] = [
          {
            id: 1,
            numeroAppartement: 'A101',
            etage: 1,
            superficie: 50,
            adresse: '123 Main St',
            type: 'studio'
          },
          {
            id: 2,
            numeroAppartement: 'B202',
            etage: 2,
            superficie: 75,
            adresse: '456 Elm St',
            type: 'T2'
          }
        ];
        return of(mockData);
      }

      getHousingUnitById(id: number): Observable<HousingUnit> {
        // return this.http.get<HousingUnit>(`${this.apiUrl}/${id}`);
        const mockData: HousingUnit = {
          id,
          numeroAppartement: 'A101',
          etage: 1,
          superficie: 50,
          adresse: '123 Main St',
          type: 'studio'
        };
        return of(mockData);
      }

      createHousingUnit(housingUnit: HousingUnit): Observable<HousingUnit> {
        // return this.http.post<HousingUnit>(this.apiUrl, housingUnit);
        const mockData: HousingUnit = { ...housingUnit, id: Math.floor(Math.random() * 1000) };
        return of(mockData);
      }

      updateHousingUnit(id: number, housingUnit: HousingUnit): Observable<HousingUnit> {
        // return this.http.put<HousingUnit>(`${this.apiUrl}/${id}`, housingUnit);
        const mockData: HousingUnit = { ...housingUnit, id };
        return of(mockData);
      }

      deleteHousingUnit(id: number): Observable<void> {
        // return this.http.delete<void>(`${this.apiUrl}/${id}`);
        return of(undefined);
      }
    }
