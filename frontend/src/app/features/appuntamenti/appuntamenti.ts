import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PrenotazioneResponse, PrenotazioniService } from '../../services/prenotazioni.service';
import { ToastService } from '../../services/toast.service';
import { ConsensoService } from '../../services/consenso.service';

@Component({
  selector: 'app-appuntamenti',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appuntamenti.html',
  styleUrl: './appuntamenti.css'
})
export class Appuntamenti implements OnInit {

  appuntamentiOriginali: PrenotazioneResponse[] = [];
  appuntamenti: PrenotazioneResponse[] = [];

  // Filtri Base
  searchQuery: string = '';
  filtroDal: string = '';
  filtroAl: string = '';

  // Ordine
  sortDirAmbulatorio: boolean = true;
  sortDirPaziente: boolean = true;
  sortDirData: boolean = true;
  sortDirOra: boolean = true;

  // Dettagli Modale
  dettaglioAperto: boolean = false;
  appSelected: PrenotazioneResponse | null = null;
  modaleTipoVisibile: 'DETTAGLI' | 'CONSENSO' = 'DETTAGLI';
  // Form Consenso
  consensoTypologia: string = '';
  consensoFile: File | null = null;

  constructor(
    private prenotazioniService: PrenotazioniService,
    private consensoService: ConsensoService,
    private router: Router,
    private toast: ToastService
  ) {}

  ngOnInit() {
    this.caricaAppuntamenti();
  }

  caricaAppuntamenti() {
    this.prenotazioniService.getPrenotazioni().subscribe({
      next: (data) => {
        this.appuntamentiOriginali = data;
        this.appuntamenti = [...data];
      },
      error: () => this.toast.show("Errore nel caricamento appuntamenti", "error")
    });
  }

  vaiAAggiungi() {
    this.router.navigate(['/appuntamenti/nuovo']);
  }

  apriDettaglio(app: PrenotazioneResponse) {
    this.appSelected = app;
    this.dettaglioAperto = true;
    this.modaleTipoVisibile = 'DETTAGLI';
  }

  chiudiDettaglio() {
    this.dettaglioAperto = false;
    this.appSelected = null;
    this.consensoTypologia = '';
    this.consensoFile = null;
  }

  // Azioni Modale
  vaiAElaboraTransazione() {
    if(this.appSelected) {
      this.router.navigate(['/appuntamenti/transazione', this.appSelected.id]);
    }
  }

  accettaCliente() {
    if(this.appSelected) {
      this.prenotazioniService.accettaCliente(this.appSelected.id).subscribe({
        next: (res) => {
          this.toast.show("Cliente accettato con successo!", "success");
          this.caricaAppuntamenti();
          this.chiudiDettaglio();
        },
        error: (err) => this.toast.show(err.error || "Errore accettazione", "error")
      });
    }
  }

  cancellaPrenotazione() {
    if (this.appSelected) {
      if(confirm("Sicuro di voler annullare questa prenotazione?")) {
        this.prenotazioniService.cancellaPrenotazione(this.appSelected.id).subscribe({
          next: () => {
            this.toast.show("Prenotazione annullata", "success");
            this.caricaAppuntamenti();
            this.chiudiDettaglio();
          },
          error: (err) => this.toast.show(err.error || "Impossibile annullare", "error")
        });
      }
    }
  }

  apriConsensi() {
    this.modaleTipoVisibile = 'CONSENSO';
  }

  onFileSelected(event: any) {
    this.consensoFile = event.target.files[0];
  }

  salvaConsenso() {
    if (this.appSelected && this.consensoFile && this.consensoTypologia) {
      this.consensoService.creaConsenso(this.appSelected.cfCliente, this.consensoTypologia, this.consensoFile)
        .subscribe({
          next: () => {
            this.toast.show("Consenso caricato con successo!", "success");
            this.chiudiDettaglio();
          },
          error: () => this.toast.show("Errore upload consenso", "error")
        });
    } else {
        this.toast.show("Compila tipo e seleziona il file PDF", "error");
    }
  }

  // Ricerca Globale
  applicaFiltroRicerca() {
    let lowerQuery = this.searchQuery.toLowerCase();
    this.appuntamenti = this.appuntamentiOriginali.filter(a => 
      a.nomeCliente?.toLowerCase().includes(lowerQuery) ||
      a.cognomeCliente?.toLowerCase().includes(lowerQuery) ||
      a.cfCliente?.toLowerCase().includes(lowerQuery) ||
      a.codiceAmbulatorio?.toLowerCase().includes(lowerQuery)
    );
  }

  // Filtro Date Range
  applicaFiltroDate() {
    this.appuntamenti = this.appuntamentiOriginali.filter(a => {
      let passed = true;
      let d = new Date(a.dataPrenotazione);
      if (this.filtroDal) {
         if (d < new Date(this.filtroDal)) passed = false;
      }
      if (this.filtroAl) {
         if (d > new Date(this.filtroAl)) passed = false;
      }
      return passed;
    });
  }

  // Tasti Sort
  sort(by: 'paziente' | 'ambulatorio' | 'data' | 'ora') {
    if (by === 'paziente') {
        this.appuntamenti.sort((a,b) => this.sortDirPaziente ? (a.cognomeCliente || "").localeCompare(b.cognomeCliente || "") : (b.cognomeCliente || "").localeCompare(a.cognomeCliente || ""));
        this.sortDirPaziente = !this.sortDirPaziente;
    } else if (by === 'ambulatorio') {
        this.appuntamenti.sort((a,b) => this.sortDirAmbulatorio ? (a.codiceAmbulatorio || "").localeCompare(b.codiceAmbulatorio || "") : (b.codiceAmbulatorio || "").localeCompare(a.codiceAmbulatorio || ""));
        this.sortDirAmbulatorio = !this.sortDirAmbulatorio;
    } else if (by === 'data') {
        this.appuntamenti.sort((a,b) => {
            let res = new Date(a.dataPrenotazione).getTime() - new Date(b.dataPrenotazione).getTime();
            return this.sortDirData ? res : -res;
        });
        this.sortDirData = !this.sortDirData;
    } else if (by === 'ora') {
        this.appuntamenti.sort((a,b) => {
            let res = a.orarioPrenotazione.localeCompare(b.orarioPrenotazione);
            return this.sortDirOra ? res : -res;
        });
        this.sortDirOra = !this.sortDirOra;
    }
  }
}
