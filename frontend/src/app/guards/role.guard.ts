import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Route Guard basata sui ruoli.
 *
 * Nella configurazione delle route aggiungi:
 *   canActivate: [roleGuard],
 *   data: { roles: ['Addetto al Front-Office'] }
 *
 * Se l'utente non è autenticato → redirect a /login
 * Se l'utente è autenticato ma non ha il ruolo richiesto → redirect a /dashboard (home sicura)
 */
export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  // 1. Verifica autenticazione (token valido e non scaduto)
  if (!auth.isLoggedIn()) {
    return router.parseUrl('/login');
  }

  // 2. Leggi i ruoli richiesti dalla configurazione della rotta
  const requiredRoles: string[] = route.data?.['roles'] ?? [];

  // 3. Se nessun ruolo è richiesto, basta essere loggati
  if (requiredRoles.length === 0) {
    return true;
  }

  // 4. Verifica il matching del ruolo
  if (auth.hasRole(...requiredRoles)) {
    return true;
  }

  // 5. Accesso negato: redirect alla home sicura
  return router.parseUrl('/dashboard');
};
