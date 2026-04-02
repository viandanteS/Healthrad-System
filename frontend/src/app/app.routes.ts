import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ShellComponent } from './shell/shell.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AnagraficaComponent } from './features/anagrafica/anagrafica.component';
import { Appuntamenti } from './features/appuntamenti/appuntamenti';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

// Ruoli disponibili nel sistema
// - "Addetto al Front-Office" → accesso completo al front-office
// - "Medico specialista"      → accesso cartella clinica (da implementare)
// - "CLIENTE"                 → portale cliente (da implementare)
export const routes: Routes = [
  // Pagine pubbliche
  { path: '', component: HomeComponent, pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  // Pagine protette — shell con navbar
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      // Dashboard: accessibile a tutti gli utenti autenticati
      { path: 'dashboard', component: DashboardComponent },

      // Anagrafica: solo per il Front-Office
      {
        path: 'anagrafica',
        component: AnagraficaComponent,
        canActivate: [roleGuard],
        data: { roles: ['Addetto al Front-Office'] }
      },

      {
    path: 'appuntamenti',
    loadComponent: () => import('./features/appuntamenti/appuntamenti').then(m => m.Appuntamenti),
    canActivate: [roleGuard],
    data: { roles: ['Addetto al Front-Office'] }
  },
  {
    path: 'appuntamenti/nuovo',
    loadComponent: () => import('./features/appuntamenti/aggiungi-appuntamento.component').then(m => m.AggiungiAppuntamentoComponent),
    canActivate: [roleGuard],
    data: { roles: ['Addetto al Front-Office'] }
  },
  {
    path: 'appuntamenti/transazione/:id',
    loadComponent: () => import('./features/appuntamenti/elabora-transazione.component').then(m => m.ElaboraTransazioneComponent),
    canActivate: [roleGuard],
    data: { roles: ['Addetto al Front-Office'] }
  },

      // Cartella clinica: solo per Medico (da implementare)
      // { path: 'pazienti', component: PazientiComponent, canActivate: [roleGuard], data: { roles: ['Medico specialista'] } },
    ]
  },

  // Fallback — qualsiasi rotta non trovata torna al login
  { path: '**', redirectTo: 'login' }
];

