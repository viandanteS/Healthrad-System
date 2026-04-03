import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('access_token');
  const auth = inject(AuthService);
  const toast = inject(ToastService);

  // Inietta il token Bearer in ogni richiesta
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        if (req.url.includes('/api/auth/login')) {
           toast.show('Credenziali errate.', 'error');
        } else {
           toast.show('Sessione scaduta. Effettua nuovamente il login.', 'error');
           auth.logout();
        }
      } else if (error.status === 403) {
        toast.show('Accesso negato. Non hai i permessi per questa operazione.', 'error');
      }
      return throwError(() => error);
    })
  );
};
