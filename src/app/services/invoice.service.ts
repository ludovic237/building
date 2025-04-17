import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Invoice } from '../model/data';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  private apiUrl = '/api/invoices';

  constructor(private http: HttpClient) {}

  getInvoices(): Observable<Invoice[]> {
    // return this.http.get<Invoice[]>(this.apiUrl);
    const mockData: Invoice[] = [
      {
        id: 1,
        locataireId: 101,
        type: 'eau',
        mois: 1,
        montant: 50,
        statut: 'payée',
        datePaiement: new Date('2023-01-15')
      },
      {
        id: 2,
        locataireId: 102,
        type: 'électricité',
        mois: 2,
        montant: 75,
        statut: 'impayée',
        datePaiement: null
      }
    ];
    return of(mockData);
  }

  getInvoiceById(id: number): Observable<Invoice> {
    // return this.http.get<Invoice>(`${this.apiUrl}/${id}`);
    const mockData: Invoice = {
      id,
      locataireId: 101,
      type: 'eau',
      mois: 1,
      montant: 50,
      statut: 'payée',
      datePaiement: new Date('2023-01-15')
    };
    return of(mockData);
  }

  createInvoice(invoice: Invoice): Observable<Invoice> {
    // return this.http.post<Invoice>(this.apiUrl, invoice);
    const mockData: Invoice = { ...invoice, id: Math.floor(Math.random() * 1000) };
    return of(mockData);
  }

  updateInvoice(id: number, invoice: Invoice): Observable<Invoice> {
    // return this.http.put<Invoice>(`${this.apiUrl}/${id}`, invoice);
    const mockData: Invoice = { ...invoice, id };
    return of(mockData);
  }

  deleteInvoice(id: number): Observable<void> {
    // return this.http.delete<void>(`${this.apiUrl}/${id}`);
    return of(undefined);
  }
}
