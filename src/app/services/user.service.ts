import { Injectable } from '@angular/core';
          import {HttpClient, HttpHeaders} from '@angular/common/http';
          import { Observable, of } from 'rxjs';
          import { User } from '../model/data';

          @Injectable({
            providedIn: 'root'
          })
          export class UserService {
            private apiUrl = '/api/users';

            constructor(private http: HttpClient) {}

            private getHeaders(): HttpHeaders {
              const token = localStorage.getItem('token');
              return new HttpHeaders({
                'Authorization': `Bearer ${token}`
              });
            }

            getUsers(): Observable<User[]> {
              return this.http.get<User[]>(this.apiUrl, {headers: this.getHeaders()});
            }

            getUserById(id: number): Observable<User> {
              return this.http.get<User>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
            }

            createUser(user: User): Observable<User> {
              return this.http.post<User>(this.apiUrl, user, {headers: this.getHeaders()});
            }

            updateUser(id: number, user: User): Observable<User> {
              return this.http.put<User>(`${this.apiUrl}/${id}`, user, {headers: this.getHeaders()});
            }

            deleteUser(id: number): Observable<void> {
              return this.http.delete<void>(`${this.apiUrl}/${id}`, {headers: this.getHeaders()});
            }
          }
