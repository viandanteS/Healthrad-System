import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConsensoService {
  private apiUrl = `http://localhost:8080/api/consensi`;

  constructor(private http: HttpClient) { }

  creaConsenso(cfCliente: string, tipologia: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('cfCliente', cfCliente);
    formData.append('tipologia', tipologia);
    formData.append('file', file);
    
    return this.http.post(this.apiUrl, formData);
  }
}
