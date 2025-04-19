import { Injectable } from '@angular/core';
    import {HttpClient, HttpHeaders} from '@angular/common/http';
    import { Observable, of } from 'rxjs';
    import { Service } from '../model/data';

    @Injectable({
      providedIn: 'root'
    })
    export class ServiceService {
      private apiUrl = 'http://localhost:8080/api/services';

      constructor(private http: HttpClient) {}

      private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('token');
        return new HttpHeaders({
          'Authorization': `Bearer ${token}`
        });
      }

      getServices(): Observable<Service[]> {
        return this.http.get<Service[]>(this.apiUrl,{headers: this.getHeaders()});
      }

      getServiceById(id: number): Observable<Service> {
        return this.http.get<Service>(`${this.apiUrl}/${id}`,{headers: this.getHeaders()});
      }

      createService(service: Service): Observable<Service> {
        return this.http.post<Service>(this.apiUrl, service,{headers: this.getHeaders()});
      }

      updateService(id: number, service: Service): Observable<Service> {
        return this.http.put<Service>(`${this.apiUrl}/${id}`, service,{headers: this.getHeaders()});
      }

      deleteService(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`,{headers: this.getHeaders()});
      }
    }
