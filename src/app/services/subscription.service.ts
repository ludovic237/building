import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Subscription } from '../model/data';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = '/api/subscriptions';

  constructor(private http: HttpClient) {}

  getSubscriptions(): Observable<Subscription[]> {
    // return this.http.get<Subscription[]>(this.apiUrl);
    const mockData: Subscription[] = [
      {
        id: 1,
        locataireId: 101,
        utilisateurId: undefined,
        serviceId: 1,
        dateDebut: new Date('2023-01-01'),
        dateFin: new Date('2023-12-31'),
        statut: 'active'
      },
      {
        id: 2,
        locataireId: undefined,
        utilisateurId: 201,
        serviceId: 2,
        dateDebut: new Date('2023-02-01'),
        dateFin: null,
        statut: 'inactive'
      }
    ];
    return of(mockData);
  }

  getSubscriptionById(id: number): Observable<Subscription> {
    // return this.http.get<Subscription>(`${this.apiUrl}/${id}`);
    const mockData: Subscription = {
      id,
      locataireId: 101,
      utilisateurId: undefined,
      serviceId: 1,
      dateDebut: new Date('2023-01-01'),
      dateFin: new Date('2023-12-31'),
      statut: 'active'
    };
    return of(mockData);
  }

  createSubscription(subscription: Subscription): Observable<Subscription> {
    // return this.http.post<Subscription>(this.apiUrl, subscription);
    const mockData: Subscription = { ...subscription, id: Math.floor(Math.random() * 1000) };
    return of(mockData);
  }

  updateSubscription(id: number, subscription: Subscription): Observable<Subscription> {
    // return this.http.put<Subscription>(`${this.apiUrl}/${id}`, subscription);
    const mockData: Subscription = { ...subscription, id };
    return of(mockData);
  }

  deleteSubscription(id: number): Observable<void> {
    // return this.http.delete<void>(`${this.apiUrl}/${id}`);
    return of(undefined);
  }
}
