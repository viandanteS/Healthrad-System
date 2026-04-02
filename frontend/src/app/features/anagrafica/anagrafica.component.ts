import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService, Cliente } from '../../services/cliente.service';

@Component({
  selector: 'app-anagrafica',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './anagrafica.component.html',
  styleUrl: './anagrafica.component.css'
})
export class AnagraficaComponent implements OnInit {

  // Stato reattivo
  clienti = signal<Cliente[]>([]);
  searchQuery = signal<string>('');
  isLoading = signal<boolean>(false);
  errorMsg = signal<string>('');

  // Modal nuovo cliente
  showModal = signal<boolean>(false);
  nuovoCliente: Partial<Cliente> = {};
  formError = signal<string>('');
  formSuccess = signal<string>('');

  // Modal dettaglio
  clienteSelezionato = signal<Cliente | null>(null);

  // Lista filtrata live tramite Signals
  clientiFiltrati = computed(() => {
    const q = this.searchQuery().toLowerCase();
    if (!q) return this.clienti();
    return this.clienti().filter(c =>
      c.nome.toLowerCase().includes(q) ||
      c.cognome.toLowerCase().includes(q) ||
      c.email.toLowerCase().includes(q) ||
      c.cf.toLowerCase().includes(q)
    );
  });

  constructor(private clienteService: ClienteService) {}

  ngOnInit() { this.caricaClienti(); }

  caricaClienti() {
    this.isLoading.set(true);
    this.clienteService.getClienti().subscribe({
      next: (data) => { this.clienti.set(data); this.isLoading.set(false); },
      error: () => { this.errorMsg.set('Impossibile caricare i clienti dal server.'); this.isLoading.set(false); }
    });
  }

  onSearch(event: Event) {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  // --- Modal nuovo cliente ---
  apriForm() {
    this.nuovoCliente = {};
    this.formError.set('');
    this.formSuccess.set('');
    this.showModal.set(true);
  }

  chiudiForm() { this.showModal.set(false); }

  salvaCliente() {
    this.formError.set('');
    if (!this.nuovoCliente.cf || this.nuovoCliente.cf.length !== 16) {
      this.formError.set('Il Codice Fiscale deve essere di esattamente 16 caratteri.');
      return;
    }
    if (!this.nuovoCliente.nome || !this.nuovoCliente.cognome) {
      this.formError.set('Nome e Cognome sono obbligatori.');
      return;
    }
    
    // Obbligatorietà e validazione Data di Nascita
    if (!this.nuovoCliente.dataNascita) {
      this.formError.set('La data di nascita è obbligatoria.');
      return;
    }
    const dataOggi = new Date();
    const dataSelezionata = new Date(this.nuovoCliente.dataNascita);
    if (dataSelezionata > dataOggi) {
      this.formError.set('La data di nascita non può essere nel futuro.');
      return;
    }

    // Validazione email solo se presente
    if (this.nuovoCliente.email && this.nuovoCliente.email.trim() !== '') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.nuovoCliente.email)) {
        this.formError.set('Il formato dell\'email non è valido.');
        return;
      }
    }

    this.clienteService.creaCliente(this.nuovoCliente).subscribe({
      next: (saved) => {
        this.formSuccess.set(`Cliente ${saved.nome} ${saved.cognome} creato con successo!`);
        this.caricaClienti();
        setTimeout(() => this.chiudiForm(), 1800);
      },
      error: (err) => { 
        let errorMsg = 'Errore nella creazione del cliente.';
        if (typeof err.error === 'string') {
          errorMsg = err.error;
        } else if (err.error && err.error.message) {
          errorMsg = err.error.message;
        } else if (err.error && err.error.error === 'Internal Server Error') {
          errorMsg = 'Errore 500. Potrebbe essere un duplicato o campo non valido.';
        } else if (err.message) {
          errorMsg = err.message;
        }
        this.formError.set(errorMsg); 
      }
    });
  }

  // --- Modal dettaglio / modifica ---
  clienteInModifica = signal<boolean>(false);
  clienteEditData: Partial<Cliente> = {};

  apriDettaglio(cliente: Cliente) { 
    this.clienteSelezionato.set(cliente); 
    this.clienteInModifica.set(false);
    this.formError.set('');
    this.formSuccess.set('');
  }
  
  chiudiDettaglio() { 
    this.clienteSelezionato.set(null); 
    this.clienteInModifica.set(false);
  }

  abilitaModifica() {
    this.clienteInModifica.set(true);
    // Creiamo una copia profonda per non sporcare i dati in tabella
    this.clienteEditData = { ...this.clienteSelezionato()! };
    this.formError.set('');
    this.formSuccess.set('');
  }

  salvaModifiche() {
    this.formError.set('');
    const cf = this.clienteSelezionato()?.cf;
    if (!cf) return;
    
    if (!this.clienteEditData.nome || !this.clienteEditData.cognome) {
      this.formError.set('Nome e Cognome sono obbligatori.');
      return;
    }

    // Obbligatorietà e validazione Data di Nascita
    if (!this.clienteEditData.dataNascita) {
      this.formError.set('La data di nascita è obbligatoria.');
      return;
    }
    const dataOggi = new Date();
    const dataSelezionata = new Date(this.clienteEditData.dataNascita);
    if (dataSelezionata > dataOggi) {
      this.formError.set('La data di nascita non può essere nel futuro.');
      return;
    }

    // Validazione email solo se presente
    if (this.clienteEditData.email && this.clienteEditData.email.trim() !== '') {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(this.clienteEditData.email)) {
        this.formError.set('Il formato dell\'email non è valido.');
        return;
      }
    }

    this.clienteService.aggiornaCliente(cf, this.clienteEditData).subscribe({
      next: (updated) => {
        this.formSuccess.set('Dati cliente aggiornati!');
        this.caricaClienti(); // ricarica elenco
        this.clienteSelezionato.set(updated); // aggiorna modale con i nuovi dati
        setTimeout(() => this.clienteInModifica.set(false), 1500);
      },
      error: (err) => { 
        let errorMsg = 'Errore durante l\'aggiornamento dei dati.';
        if (typeof err.error === 'string') {
          errorMsg = err.error;
        } else if (err.error && err.error.message) {
          errorMsg = err.error.message;
        } else if (err.error && err.error.error === 'Internal Server Error') {
          errorMsg = 'Errore 500. Potrebbe essere un duplicato o campo non valido.';
        } else if (err.message) {
          errorMsg = err.message;
        }
        this.formError.set(errorMsg); 
      }
    });
  }
}
