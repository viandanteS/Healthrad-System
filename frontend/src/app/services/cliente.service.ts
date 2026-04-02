import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  cf: string;
  nome: string;
  cognome: string;
  dataNascita: string;
  telefono: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private apiUrl = 'http://localhost:8080/api/clienti';

  constructor(private http: HttpClient) { }

  getClienti(search?: string): Observable<Cliente[]> {
    let params = new HttpParams();
    if (search) params = params.set('q', search);
    return this.http.get<Cliente[]>(this.apiUrl, { params });
  }

  creaCliente(cliente: Partial<Cliente>): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  aggiornaCliente(cf: string, data: Partial<Cliente>): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${cf}`, data);
  }
}
