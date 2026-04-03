import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { PrenotazioniService } from '../../services/prenotazioni.service';
import { ToastService } from '../../services/toast.service';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
  selector: 'app-aggiungi-appuntamento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './aggiungi-appuntamento.component.html'
})
export class AggiungiAppuntamentoComponent implements OnInit {

  // Dati Utente
  cfRicevuto: string = '';
  clienteTrovato: any = null;
  clientiSuggeriti: any[] = [];
  cfSubject: Subject<string> = new Subject<string>();

  // Form Appuntamento
  tipologiaVisita: string = '';
  ambulatorio: string = '';
  dataPrevista: string = '';
  minDate: string = new Date().toISOString().split('T')[0];
  orarioSelezionato: string = '';

  tipologie = ['RX Torace', 'Risonanza Magnetica', 'Ecografia', 'TAC', 'Visita Specialistica'];
  ambulatori = ['A01', 'A02', 'B01', 'B02', 'C01'];
  orariPossibili = ['09:00', '09:30', '10:00', '10:30', '11:00', '11:30', '14:00', '14:30', '15:00', '15:30', '16:00', '16:30'];
  orariOcupati: string[] = [];

  constructor(
    private http: HttpClient,
    private service: PrenotazioniService,
    private toast: ToastService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
        if(params['cf']) {
            this.cfRicevuto = params['cf'];
            this.http.get(`http://localhost:8080/api/clienti/cerca?cf=${this.cfRicevuto}`).subscribe({
                next: (res: any) => this.selezionaCliente(res),
                error: () => {}
            });
        }
    });

    this.cfSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if(query.length >= 2 && !this.clienteTrovato) {
         this.cercaClientiSuggeriti(query);
      } else if (query.length < 2) {
         this.clientiSuggeriti = [];
      }
    });
  }

  onCfChange() {
    this.clienteTrovato = null; // Resetta il cliente selezionato se l'utente digita ancora
    this.cfSubject.next(this.cfRicevuto.toUpperCase());
  }

  cercaClientiSuggeriti(query: string) {
    this.http.get(`http://localhost:8080/api/clienti?q=${query}`).subscribe({
      next: (res: any) => {
        this.clientiSuggeriti = res;
      },
      error: () => {
        this.clientiSuggeriti = [];
      }
    });
  }

  selezionaCliente(cliente: any) {
    this.clienteTrovato = cliente;
    this.cfRicevuto = cliente.cf;
    this.clientiSuggeriti = [];
  }

  mostraTuttiIClienti() {
    this.clienteTrovato = null;
    this.http.get(`http://localhost:8080/api/clienti`).subscribe({
      next: (res: any) => {
        this.clientiSuggeriti = res;
      },
      error: () => {
        this.clientiSuggeriti = [];
      }
    });
  }

  onDataOAmbulatorioChange() {
    this.orarioSelezionato = '';
    if (this.dataPrevista && this.ambulatorio) {
      this.service.getOrariOccupati(this.dataPrevista, this.ambulatorio).subscribe({
        next: (occupati) => {
          // La query ritorna array di stringhe HH:mm:ss o HH:mm
          this.orariOcupati = occupati.map(o => o.substring(0, 5));
        }
      });
    }
  }

  isOccupato(ora: string): boolean {
    return this.orariOcupati.includes(ora);
  }

  conferma() {
    if(!this.clienteTrovato) {
       this.toast.show("Devi selezionare un cliente valido prima", "error"); return;
    }
    if(!this.tipologiaVisita || !this.ambulatorio || !this.dataPrevista || !this.orarioSelezionato) {
       this.toast.show("Compila tutti i campi!", "error"); return;
    }

    const payload = {
        stato: 'Prenotato',
        dataPrenotazione: this.dataPrevista,
        orarioPrenotazione: this.orarioSelezionato + ":00",
        tipologia: this.tipologiaVisita,
        ambulatorio: { codiceAmbulatorio: this.ambulatorio },
        cliente: { cf: this.clienteTrovato.cf },
        addetto: { cf: 'FO00000000000001' } // TODO: recuperare da utente loggato dal claims JWT, metto default temporaneo
    };

    this.service.aggiungiPrenotazione(payload).subscribe({
      next: () => {
        this.toast.show("Appuntamento confermato con successo", "success");
        this.router.navigate(['/appuntamenti']);
      },
      error: (e) => this.toast.show(e.error || "Errore durante salvataggio", "error")
    });
  }
}
