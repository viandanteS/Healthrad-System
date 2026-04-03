import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
  mostraMenuFiltri: boolean = false;
  mostraMenuCalendario: boolean = false;
  sortDirAmbulatorio: boolean = true;
  sortDirPaziente: boolean = true;
  sortDirData: boolean = true;
  sortDirOra: boolean = true;

  // Dettagli Modale
  dettaglioAperto: boolean = false;
  appSelected: PrenotazioneResponse | null = null;
  modaleTipoVisibile: 'DETTAGLI' | 'CONSENSO' | 'MODIFICA' = 'DETTAGLI';
  // Form Consenso
  consensoTypologia: string = '';
  consensoFile: File | null = null;

  // Form Modifica
  editTipologia: string = '';
  editAmbulatorio: string = '';
  editData: string = '';
  editOra: string = '';
  minDate: string = new Date().toISOString().split('T')[0];
  tipologie = ['RX Torace', 'Risonanza Magnetica', 'Ecografia', 'TAC', 'Visita Specialistica'];
  ambulatori = ['A01', 'A02', 'B01', 'B02', 'C01'];
  ambulatoriAttivi: string[] = [];
  orariDisponibili: string[] = [];

  constructor(
    private prenotazioniService: PrenotazioniService,
    private consensoService: ConsensoService,
    private router: Router,
    private route: ActivatedRoute,
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
        
        // Se c`è un query parameter cf, filtra automaticamente
        this.route.queryParams.subscribe(params => {
           if(params['cf']) {
               this.searchQuery = params['cf'];
               this.applicaFiltroRicerca();
           }
        });
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
    this.caricaConsenso();
  }

  apriModifica() {
    if (this.appSelected) {
      if (this.appSelected.stato !== 'Prenotato') {
        this.toast.show("Puoi modificare solo appuntamenti in stato 'Prenotato'.", "error");
        return;
      }
      this.editTipologia = this.appSelected.tipologia || '';
      this.editAmbulatorio = this.appSelected.codiceAmbulatorio || '';
      this.editData = this.appSelected.dataPrenotazione || '';
      this.editOra = this.appSelected.orarioPrenotazione?.substring(0, 5) || '';
      this.modaleTipoVisibile = 'MODIFICA';
      this.onEditChange();
    }
  }

  onEditChange() {
    if (this.editData) {
        // Carica ambulatori attivi per la data
        this.prenotazioniService.getAmbulatoriDisponibili(this.editData).subscribe({
            next: (data) => this.ambulatoriAttivi = data,
            error: () => this.ambulatoriAttivi = []
        });

        if (this.editAmbulatorio) {
            this.prenotazioniService.getOrariDisponibili(this.editData, this.editAmbulatorio).subscribe({
                next: (data) => {
                    this.orariDisponibili = data.map(o => o.substring(0, 5));
                    if (this.editOra && !this.orariDisponibili.includes(this.editOra)) {
                        this.orariDisponibili.unshift(this.editOra);
                        this.orariDisponibili.sort();
                    }
                },
                error: () => this.orariDisponibili = []
            });
        }
    }
  }

  isAmbulatorioDisponibile(cod: string): boolean {
    return this.ambulatoriAttivi.includes(cod);
  }



  salvaModifica() {
    if (!this.appSelected) return;
    const payload = {
      tipologia: this.editTipologia,
      ambulatorio: { codiceAmbulatorio: this.editAmbulatorio },
      dataPrenotazione: this.editData,
      orarioPrenotazione: this.editOra + ':00'
    };
    this.prenotazioniService.modificaPrenotazione(this.appSelected.id, payload).subscribe({
      next: () => {
        this.toast.show("Prenotazione aggiornata con successo!", "success");
        this.caricaAppuntamenti();
        this.chiudiDettaglio();
      },
      error: (err) => this.toast.show(err.error || "Errore durante la modifica", "error")
    });
  }

  onFileSelected(event: any) {
    this.consensoFile = event.target.files[0];
  }

  salvaConsenso() {
    if (this.appSelected && this.consensoFile && this.consensoTypologia) {
      this.consensoService.creaConsenso(this.appSelected.cfCliente, this.appSelected.id, this.consensoTypologia, this.consensoFile)
        .subscribe({
          next: () => {
            this.toast.show("Consenso caricato con successo!", "success");
            this.chiudiDettaglio();
          },
          error: (err) => this.toast.show(err.error || "Errore upload consenso", "error")
        });
    } else {
        this.toast.show("Compila tipo e seleziona il file PDF", "error");
    }
  }

  consensoEsistente: any = null;
  caricaConsenso() {
    if (this.appSelected) {
      this.consensoService.getConsensoByPrenotazione(this.appSelected.id).subscribe({
        next: (res) => this.consensoEsistente = res,
        error: () => this.consensoEsistente = null
      });
    }
  }

  scaricaFileConsenso() {
    if(this.consensoEsistente) {
      this.consensoService.downloadConsenso(this.consensoEsistente.id).subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = this.consensoEsistente.nomeFile || 'consenso.pdf';
          a.click();
        },
        error: () => this.toast.show("Errore download file", "error")
      });
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

