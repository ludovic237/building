import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable, of } from 'rxjs';
    import { Tenant } from '../model/data';

    @Injectable({
      providedIn: 'root'
    })
    export class TenantService {
      private apiUrl = '/api/tenants';

      constructor(private http: HttpClient) {}

      getTenants(): Observable<Tenant[]> {
        // return this.http.get<Tenant[]>(this.apiUrl);
        const mockData: Tenant[] = [
          {
            id: 1,
            userId: 101,
            dateEntree: new Date('2023-01-01'),
            dateSortie: null,
            logementId: 201,
            depotGarantie: 1000
          },
          {
            id: 2,
            userId: 102,
            dateEntree: new Date('2022-06-15'),
            dateSortie: new Date('2023-06-15'),
            logementId: 202,
            depotGarantie: 1200
          }
        ];
        return of(mockData);
      }

      getTenantById(id: number): Observable<Tenant> {
        // return this.http.get<Tenant>(`${this.apiUrl}/${id}`);
        const mockData: Tenant = {
          id,
          userId: 101,
          dateEntree: new Date('2023-01-01'),
          dateSortie: null,
          logementId: 201,
          depotGarantie: 1000
        };
        return of(mockData);
      }

      createTenant(tenant: Tenant): Observable<Tenant> {
        // return this.http.post<Tenant>(this.apiUrl, tenant);
        const mockData: Tenant = { ...tenant, id: Math.floor(Math.random() * 1000) };
        return of(mockData);
      }

      updateTenant(id: number, tenant: Tenant): Observable<Tenant> {
        // return this.http.put<Tenant>(`${this.apiUrl}/${id}`, tenant);
        const mockData: Tenant = { ...tenant, id };
        return of(mockData);
      }

      deleteTenant(id: number): Observable<void> {
        // return this.http.delete<void>(`${this.apiUrl}/${id}`);
        return of(undefined);
      }
    }
