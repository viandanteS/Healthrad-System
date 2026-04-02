import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('access_token');

  if (token) {
    // Esiste un token nel localStorage
    return true;
  }

  // Nessun token: reindirizza al login
  return router.parseUrl('/login');
};
