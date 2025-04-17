import { Injectable } from '@angular/core';
    import { HttpClient } from '@angular/common/http';
    import { Observable, of } from 'rxjs';
    import { Service } from '../model/data';

    @Injectable({
      providedIn: 'root'
    })
    export class ServiceService {
      private apiUrl = '/api/services';

      constructor(private http: HttpClient) {}

      getServices(): Observable<Service[]> {
        // return this.http.get<Service[]>(this.apiUrl);
        const mockData: Service[] = [
          {
            id: 1,
            nom: 'Cleaning',
            description: 'Weekly cleaning service',
            prixMensuel: 100
          },
          {
            id: 2,
            nom: 'Maintenance',
            description: 'Monthly maintenance service',
            prixMensuel: 200
          }
        ];
        return of(mockData);
      }

      getServiceById(id: number): Observable<Service> {
        // return this.http.get<Service>(`${this.apiUrl}/${id}`);
        const mockData: Service = {
          id:1,
          nom: 'Cleaning',
          description: 'Weekly cleaning service',
          prixMensuel: 100
        };
        return of(mockData);
      }

      createService(service: Service): Observable<Service> {
        // return this.http.post<Service>(this.apiUrl, service);
        const mockData: Service = { ...service, id: Math.floor(Math.random() * 1000) };
        return of(mockData);
      }

      updateService(id: number, service: Service): Observable<Service> {
        // return this.http.put<Service>(`${this.apiUrl}/${id}`, service);
        const mockData: Service = { ...service, id };
        return of(mockData);
      }

      deleteService(id: number): Observable<void> {
        // return this.http.delete<void>(`${this.apiUrl}/${id}`);
        return of(undefined);
      }
    }
