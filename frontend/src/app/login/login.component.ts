import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  mockTokenMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.errorMessage = '';
    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        this.successMessage = "Accesso riuscito! Benvenuto " + response.utente;
        setTimeout(() => this.router.navigate(['/dashboard']), 1000);
      },
      error: (err) => {
        let msg = 'Credenziali non valide';
        if (typeof err.error === 'string') msg = err.error;
        else if (err.error?.message) msg = err.error.message;
        this.errorMessage = msg;
      }
    });
  }
}
