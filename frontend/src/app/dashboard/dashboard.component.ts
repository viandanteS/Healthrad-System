import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  nomeUtente = 'Utente';

  ngOnInit() {
    const saved = localStorage.getItem('utente_nome');
    if (saved) this.nomeUtente = saved;
  }
}
