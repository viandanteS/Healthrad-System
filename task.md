# Checklist Sviluppo Modulo Front-Office HealthRad

- [x] **Fase 1: Database Setup**
  - [x] Creare la struttura di cartelle base per il BE (es. `src/main/resources`).
  - [x] Compilare lo script SQL completo (`schema.sql`) applicando la gerarchia ISA per Dipendente/Cliente ed esportando i vincoli di CHECK.

- [ ] **Fase 2: Sviluppo Backend (Spring Boot)**
  - [ ] Generare architettura base del progetto Spring Boot.
  - [ ] Creare config DB e file `application.properties`/`yml`.
  - [ ] Strutturare le `@Entity` mappate allo schema.
  - [ ] Imbastire le Interfacce `JpaRepository`.
  - [ ] Sviluppare Servizi e vincoli logici (`PrenotazioneService`, `RicevutaService`).
  - [ ] Endpoint Fake JWT in `AuthController`.
  - [ ] Sviluppare REST API `ClienteController`, `PrenotazioneController`, `RicevutaController`.

- [ ] **Fase 3: Sviluppo Frontend (Angular)**
  - [ ] Creare progetto Angular 17+ Standalone in `/frontend`.
  - [ ] Installare e inizializzare TailwindCSS.
  - [ ] Definire modelli Typescript base e l'Interceptor JWT.
  - [ ] Creare Data Services per comunicazione HTTP col BE.
  - [ ] Strutturare `@Component` Anagrafica Clienti (CRUD form reactivo e lista).
  - [ ] Sviluppare UI Gestione Appuntamenti (Tabella, Signals, azioni "Accetta").
  - [ ] Sviluppare CassaModalComponent con lo split-payment live via `computed()`.

- [ ] **Fase 4: Integrazione e Validazione**
  - [ ] Validare inserimento da interfaccia Web fino a DB.
  - [ ] Verificare che lo split payment sul FE dialoghi col controllo in `@Transactional` sul BE senza rotture.
