import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PrenotazioneResponse, PrenotazioniService } from '../../services/prenotazioni.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-elabora-transazione',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './elabora-transazione.component.html'
})
export class ElaboraTransazioneComponent implements OnInit {

  appuntamento: PrenotazioneResponse | null = null;
  quotaAssicurazione: number = 0;
  quotaCliente: number = 50.00; // Valore fittizio di base

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: PrenotazioniService,
    private toast: ToastService
  ) {}

  ngOnInit() {
    // Per semplicità ricarichiamo l'elenco e troviamo l'ID per mostrare i dati di riepilogo
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.service.getPrenotazioni().subscribe(list => {
         const found = list.find(l => l.id === +idParam);
         if (found) {
             this.appuntamento = found;
             // Modifica costo fittizio in base al tipo
             if(this.appuntamento.tipologia === 'Risonanza Magnetica') this.quotaCliente = 120.00;
             else if (this.appuntamento.tipologia === 'TAC') this.quotaCliente = 90.00;
         } else {
             this.toast.show("Appuntamento non trovato", "error");
             this.router.navigate(['/appuntamenti']);
         }
      });
    }
  }

  confermaSalda() {
    if(this.appuntamento) {
       this.service.saldaPrenotazione(this.appuntamento.id).subscribe({
          next: () => {
             this.toast.show("Pagamento elaborato e ricevuta emessa!", "success");
             this.router.navigate(['/appuntamenti']);
          },
          error: (err) => this.toast.show(err.error || "Errore saldo", "error")
       });
    }
  }

  indietro() {
    this.router.navigate(['/appuntamenti']);
  }
}
