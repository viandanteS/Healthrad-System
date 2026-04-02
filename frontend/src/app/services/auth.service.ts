import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((res: any) => {
        if (res.access_token) {
          localStorage.setItem('access_token', res.access_token);
          localStorage.setItem('utente_nome', res.utente);
          localStorage.setItem('utente_role', res.role ?? '');
        }
      })
    );
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('access_token');
    if (!token) return false;
    // Verifica scadenza decoding il payload (senza librerie)
    try {
      const payload = this.decodePayload(token);
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getRole(): string {
    return localStorage.getItem('utente_role') ?? '';
  }

  getNome(): string {
    return localStorage.getItem('utente_nome') ?? 'Utente';
  }

  /**
   * Verifica se l'utente ha uno dei ruoli specificati.
   * Usato dalla direttiva *appHasRole e dalla RoleGuard.
   */
  hasRole(...roles: string[]): boolean {
    const userRole = this.getRole();
    return roles.some(r => userRole === r || userRole.toLowerCase().includes(r.toLowerCase()));
  }

  /**
   * Decodifica il payload JWT (base64url) senza verifica firma.
   * La verifica della firma avviene sul backend.
   */
  decodePayload(token: string): any {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  }

  logout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('utente_nome');
    localStorage.removeItem('utente_role');
    this.router.navigate(['/login']);
  }
}

