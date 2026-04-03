import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConsensoService {
  private apiUrl = `http://localhost:8080/api/consensi`;

  constructor(private http: HttpClient) { }

  creaConsenso(cfCliente: string, idPrenotazione: number, tipologia: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('cfCliente', cfCliente);
    formData.append('idPrenotazione', idPrenotazione.toString());
    formData.append('tipologia', tipologia);
    formData.append('file', file);
    
    return this.http.post(this.apiUrl, formData);
  }

  downloadConsenso(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download/${id}`, { responseType: 'blob' });
  }

  getConsensoByPrenotazione(idPrenotazione: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/prenotazione/${idPrenotazione}`);
  }
}
