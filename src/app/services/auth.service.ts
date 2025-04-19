import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {User, UserNew} from "../model/data";

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {
  }

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, {username, password});
  }

  registerUser(firstName: string, lastName: string, role: string, phone: string, email: string, password: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, {firstName, lastName, role, phone, email, password});
  }

  saveUser(user:UserNew): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, user);
  }

  logout(): Observable<any> {
    return this.http.post(`${this.baseUrl}/logout`, {});
  }
}
