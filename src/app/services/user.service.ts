import { Injectable } from '@angular/core';
          import { HttpClient } from '@angular/common/http';
          import { Observable, of } from 'rxjs';
          import { User } from '../model/data';

          @Injectable({
            providedIn: 'root'
          })
          export class UserService {
            private apiUrl = '/api/users';

            constructor(private http: HttpClient) {}

            getUsers(): Observable<User[]> {
              // return this.http.get<User[]>(this.apiUrl);
              const mockData: User[] = [
                {
                  id: 1,
                  nom: 'Doe',
                  prenom: 'John',
                  email: 'john.doe@example.com',
                  telephone: '1234567890',
                  role: 'locataire',
                  motDePasse: 'password123'
                },
                {
                  id: 2,
                  nom: 'Smith',
                  prenom: 'Jane',
                  email: 'jane.smith@example.com',
                  telephone: '0987654321',
                  role: 'visiteur',
                  motDePasse: 'password456'
                }
              ];
              return of(mockData);
            }

            getUserById(id: number): Observable<User> {
              // return this.http.get<User>(`${this.apiUrl}/${id}`);
              const mockData: User = {
                id,
                nom: 'Doe',
                prenom: 'John',
                email: 'john.doe@example.com',
                telephone: '1234567890',
                role: 'locataire',
                motDePasse: 'password123'
              };
              return of(mockData);
            }

            createUser(user: User): Observable<User> {
              // return this.http.post<User>(this.apiUrl, user);
              const mockData: User = { ...user, id: Math.floor(Math.random() * 1000) };
              return of(mockData);
            }

            updateUser(id: number, user: User): Observable<User> {
              // return this.http.put<User>(`${this.apiUrl}/${id}`, user);
              const mockData: User = { ...user, id };
              return of(mockData);
            }

            deleteUser(id: number): Observable<void> {
              // return this.http.delete<void>(`${this.apiUrl}/${id}`);
              return of(undefined);
            }
          }
