# Obiettivo: Modulo Front-Office per HealthRad S.r.l.

Sviluppo di un modulo software per il sistema informativo di una clinica medica, focalizzato sulle operazioni dell'utente "Addetto al Front-Office". Il sistema adotterà un'architettura **client-server** netta, separando il backend (Spring Boot) dal frontend (Angular) e integrando un database relazionale (MySQL 8).

## Architettura e Tecnologie

*   **Frontend (/frontend)**: Angular (versione più recente supportata dalla CLI standard), pesantemente orientato all'uso di `Standalone Components` e `Signals` per una gestione dello stato reattiva e performante senza l'overhead di RxJS per stati sincroni. Stile tramite **TailwindCSS**. Modelli aggiornati del *control flow* (`@if`, `@for`).
*   **Backend (/backend)**: Spring Boot 3.x (Java 17/21). Segue il pattern **MVC (Controller-Service-Repository-Entity)** per disaccoppiare la logica di business.
*   **Database**: MySQL 8.
*   **Sicurezza e GDPR (Best Practices Avanzate)**: 
    *   **Doppio token (Access JWT + Refresh Token)**. L'access token servirà le richieste API con breve scadenza. Alla scadenza, il refresh token (salvato fisicamente su DB) emetterà l'update.
    *   **Tokens via Cookie**: I token viaggeranno tramite cookie `HttpOnly` e `Secure` (prevenzione XSS completa). Non useremo il `localStorage`.
    *   **Firma Asimmetrica**: Codifica tramite algoritmo **RS256**, con chiave privata non esposta per l'emissione e verifica robusta, oltre ad assenza voluta di *payload sensibili* in chiaro nel JWT stesso.
    *   **JTI e Blacklist**: JWT siglati con ID Universale (`jti`) tracciati all'emissione e inseriti nella tabella `token_blacklist` in caso di logout per forzare un'anagrafica sicura su access pattern velenosi.
    *   **Device Tracking e Rate Limiting**: Ogni `refresh_token` salverà IP e `user-agent` del client. Il backend utilizzerà Bucket-rate-limiting all'endpoint di refresh per impedire brute-force. Le password utente salveranno esclusivamente l'hash protetto in **BCrypt**, mentre recapiti sensibili verranno schermati a livello JPA/DB.

## Modello Dati Esteso (MySQL 8)

Sarà creato lo script `schema.sql` per inizializzare il DB con le seguenti tabelle (incluse `token_blacklist` e `refresh_token`) e vincoli approvati:

*   **Utente**: `CF` (16 char, PK), `nome` (criptato), `cognome` (criptato), `data_nascita`, `telefono` (criptato), `email` (UNIQUE, hashato/criptato deterministico), `password` (hash BCrypt). I relativi campi VARCHAR sono stati estesi nel dimensionamento per accogliere testi cifrati.
*   **Cliente**: `CF` (PK, FK → Utente[CF]).
*   **Dipendente**: `CF` (PK, FK → Utente[CF]), `ruolo` (CHECK valori consentiti tra cui 'Addetto al Front-Office').
*   **Ambulatorio**: `codice_ambulatorio` (PK).
*   **Turno** (entità di supporto per le FK di RAT): `data_turno`, `ora_inizio`, `ora_fine` (PK composta).
*   **RAT**: `codice_ambulatorio`, `data_turno`, `ora_inizio`, `ora_fine` (Fks verso Ambulatorio e Turno).
*   **Prenotazione**: `id_prenotazione` (PK), `stato`, `data_immissione`, `data_prenotazione`, `orario_prenotazione`, `codice_ambulatorio`, `CF_cliente`, `CF_addetto`, `saldata`, `tipologia`. Fks appropriate, `UNIQUE` su `(data, orario, ambulatorio)` e `CHECK (data_prenotazione >= data_immissione)`.
*   **RDT (Relazione Dipendente-Turno)**: Entità associativa `(data_turno, ora_inizio, ora_fine, cf_dipendente, presenza)` per tracciare con assoluta normalizzazione l'assegnazione dei medici. Modificata `Prenotazione` rimuovendo `cf_medico` per inferirlo in modo robusto da `RAT` e `RDT`.
*   **Ente Assicurativo**: `piva_ente` (PK), `ragione_sociale`, `indirizzo`, `recapito`.
*   **Ricevuta**: `id_ricevuta` (PK), `id_prenotazione` (FK), `data_emissione`, `importo_totale`, `quota_cliente`, `quota_assicurazione`, `piva_ente` (FK, nullable). `CHECK (importo_totale = quota_cliente + quota_assicurazione)` e vincolo logico per piva_ente basato sulla quota assicurazione.
*   **Cartella Clinica** (entità base per le FK): `id_cc` (PK).
*   **Consenso**: `id_consenso` (PK), `CF_cliente` (FK), `id_cc` (FK nullable), `tipologia`, `file`.
*   **Reclamo**: `cod_reclamo` (PK), `testo`, `CF_cliente` (FK), `CF_dipendente` (FK nullable).

## Sviluppo Backend (Spring Boot 3.x)

### Layer Entity e Repository
Mappatura JPA delle entità focalizzate per il Front-Office (Utente, Cliente, Dipendente, Ambulatorio, Prenotazione, EnteAssicurativo, Ricevuta, Turno, RAT, RDT).

### Layer Service e Storage (Logica di Business)
*   **Gestione File Storage Sicura (Proxy endpoint)**: I referti e documenti del `Consenso` NON verranno salvati su DB come BLOB (per evitare leak mnemonico). Verranno conservati in una directory sicura su server non esposta direttamente al pubblico. Il DB custodirà unicamente l'URL virtuale. L'accesso avverrà in modalità *Proxy Rest Endpoint* (`GET /api/consensi/file/{id}`), laddove il backend controllerà la validità del token JWT e l'autorizzazione dell'utente ad esaminare un determinato target prima di smistare lo stream in Bytes.
*   **PrenotazioneService**: Forza lo stato "Prenotato" alla creazione e gestisce la logica del cambio stato in "In attesa" ("Accetta Cliente"). Esegue validazioni per conflitti di prenotazione e l'esistenza di un medico coperto dalla relazione di turno (`RDT` + `RAT`) all'istante richiesto.
*   **RicevutaService**: Valida lo split payment ("ImportoTotale = QuotaCliente + QuotaAssicurazione", e l'obbligatorietà di P.IVA in caso la quota assicurativa sia presente) in un blocco `@Transactional` per garantire atomicamente l'aggiornamento di `saldata = true` sulla prenotazione.

### Layer Controller (API REST)
*   `AuthController`: fittizio, `POST /api/auth/login`.
*   `ClienteController`: `GET` (ricerca), `POST` (creazione).
*   `ConsensoController`: `POST` (multipart upload file fisici), `GET` (visione limitata da RBAC).
*   `PrenotazioneController`: `GET` (filtri), `PUT` (cambio stato).
*   `RicevutaController`: `POST` (elabora transazione).

## Sviluppo Frontend (Angular & TailwindCSS)

### Core e Configurazione
*   Integrazione di **TailwindCSS**. Interceptor JWT attivato.

### Componenti Reattivi (Signals)
*   `AnagraficaComponent`: Form reattivo per nuovo cliente e lista UI (gestito con signals e `@for`/`@if`).
*   `AppuntamentiComponent`: Tabella appuntamenti, azioni reattive per "Accetta Cliente".
*   `CassaModalComponent`: Split payment in UI in tempo reale sfruttando la fluidità di `computed()`, validando lo split in locale prima della sottomissione.

## Piano di Esecuzione
1. Crea Script SQL Schema (`backend/src/main/resources/schema.sql`).
2. Sviluppo Backend a blocchi (Entità -> Repository -> Autenticazione -> Service -> Controller).
3. Sviluppo Frontend a blocchi (Workspace -> Tailwind -> Servizi API -> UI Componenti).
4. Verifica Fine-to-End.
