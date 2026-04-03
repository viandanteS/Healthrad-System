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
  ambulatoriAttivi: string[] = [];
  orariDisponibili: string[] = [];

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

  onDataChange() {
    this.ambulatorio = '';
    this.orariDisponibili = [];
    this.orarioSelezionato = '';
    
    if (this.dataPrevista) {
      this.service.getAmbulatoriDisponibili(this.dataPrevista).subscribe({
        next: (attivi) => {
          console.log("Ambulatori attivi per la data " + this.dataPrevista + ": ", attivi);
          this.ambulatoriAttivi = attivi;
        },
        error: (err) => {
          console.error("Errore recupero ambulatori: ", err);
          this.ambulatoriAttivi = [];
        }
      });
    }

  }

  onAmbulatorioChange() {
    this.orarioSelezionato = '';
    this.orariDisponibili = [];
    
    if (this.dataPrevista && this.ambulatorio) {
      this.service.getOrariDisponibili(this.dataPrevista, this.ambulatorio).subscribe({
        next: (disponibili) => {
          this.orariDisponibili = disponibili.map(o => o.substring(0, 5));
        },
        error: () => {
          this.orariDisponibili = [];
          this.toast.show("Nessun orario disponibile per questo ambulatorio", "error");
        }
      });
    }
  }

  isAmbulatorioDisponibile(cod: string): boolean {
    return this.ambulatoriAttivi.includes(cod);
  }

  isOccupato(ora: string): boolean {
    return false; // Non più usato poiché mostriamo solo i liberi
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
