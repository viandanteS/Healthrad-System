import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css'
})
export class ShellComponent implements OnInit {
  nomeUtente = 'Utente';
  ruoloUtente = 'Addetto al front-office';

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    const saved = localStorage.getItem('utente_nome');
    if (saved) this.nomeUtente = saved;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
