import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PrenotazioneResponse {
  id: number;
  stato: string;
  dataImmissione: string;
  dataPrenotazione: string;
  orarioPrenotazione: string;
  codiceAmbulatorio: string;
  cfCliente: string;
  nomeCliente: string;
  cognomeCliente: string;
  cfMedico: string;
  nomeMedico: string;
  saldata: boolean;
  tipologia: string;
}

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniService {

  private apiUrl = `http://localhost:8080/api/prenotazioni`;

  constructor(private http: HttpClient) { }

  getPrenotazioni(): Observable<PrenotazioneResponse[]> {
    return this.http.get<PrenotazioneResponse[]>(this.apiUrl);
  }

  getOrariOccupati(data: string, ambulatorio: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/disponibilita?data=${data}&ambulatorio=${ambulatorio}`);
  }

  aggiungiPrenotazione(prenotazione: any): Observable<PrenotazioneResponse> {
    return this.http.post<PrenotazioneResponse>(this.apiUrl, prenotazione);
  }

  accettaCliente(id: number): Observable<PrenotazioneResponse> {
    return this.http.put<PrenotazioneResponse>(`${this.apiUrl}/${id}/accetta`, {});
  }

  saldaPrenotazione(id: number): Observable<PrenotazioneResponse> {
    return this.http.put<PrenotazioneResponse>(`${this.apiUrl}/${id}/salda`, {});
  }

  cancellaPrenotazione(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
