import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { HasRoleDirective } from '../directives/has-role.directive';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterModule, HasRoleDirective],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.css'
})
export class ShellComponent implements OnInit {
  nomeUtente = 'Utente';
  ruoloUtente = '';

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.nomeUtente = this.authService.getNome();
    this.ruoloUtente = this.authService.getRole() || 'Cliente';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
