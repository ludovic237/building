import { Injectable } from '@angular/core';
      import { HttpClient } from '@angular/common/http';
      import { Observable, of } from 'rxjs';
      import { Rent } from '../model/data';

      @Injectable({
        providedIn: 'root'
      })
      export class RentService {
        private apiUrl = '/api/rents';

        constructor(private http: HttpClient) {}

        getRents(): Observable<Rent[]> {
          // return this.http.get<Rent[]>(this.apiUrl);
          const mockData: Rent[] = [
            {
              id: 1,
              logementId: 101,
              locataireId: 201,
              mois: 1,
              annee: 2023,
              montant: 500,
              statut: 'payé',
              datePaiement: new Date('2023-01-05')
            },
            {
              id: 2,
              logementId: 102,
              locataireId: 202,
              mois: 2,
              annee: 2023,
              montant: 600,
              statut: 'en retard',
              datePaiement: null
            }
          ];
          return of(mockData);
        }

        getRentById(id: number): Observable<Rent> {
          // return this.http.get<Rent>(`${this.apiUrl}/${id}`);
          const mockData: Rent = {
            id,
            logementId: 101,
            locataireId: 201,
            mois: 1,
            annee: 2023,
            montant: 500,
            statut: 'payé',
            datePaiement: new Date('2023-01-05')
          };
          return of(mockData);
        }

        createRent(rent: Rent): Observable<Rent> {
          // return this.http.post<Rent>(this.apiUrl, rent);
          const mockData: Rent = { ...rent, id: Math.floor(Math.random() * 1000) };
          return of(mockData);
        }

        updateRent(id: number, rent: Rent): Observable<Rent> {
          // return this.http.put<Rent>(`${this.apiUrl}/${id}`, rent);
          const mockData: Rent = { ...rent, id };
          return of(mockData);
        }

        deleteRent(id: number): Observable<void> {
          // return this.http.delete<void>(`${this.apiUrl}/${id}`);
          return of(undefined);
        }
      }
