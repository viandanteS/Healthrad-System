CREATE TABLE IF NOT EXISTS utente (
    cf VARCHAR(16) PRIMARY KEY CHECK (CHAR_LENGTH(cf) = 16),
    nome VARCHAR(255) NOT NULL,
    cognome VARCHAR(255) NOT NULL,
    data_nascita DATE NOT NULL,
    telefono VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cliente (
    cf VARCHAR(16) PRIMARY KEY,
    FOREIGN KEY (cf) REFERENCES utente(cf) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dipendente (
    cf VARCHAR(16) PRIMARY KEY,
    ruolo VARCHAR(50) NOT NULL CHECK (ruolo IN ('Socio fondatore', 'Farmacista', 'Addetto al magazzino', 'Medico specialista', 'Tecnico di radiologia', 'Infermiere', 'Addetto al Front-Office', 'Impiegato Amministrativo')),
    FOREIGN KEY (cf) REFERENCES utente(cf) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ambulatorio (
    codice_ambulatorio VARCHAR(50) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS turno (
    data_turno DATE,
    ora_inizio TIME,
    ora_fine TIME,
    PRIMARY KEY (data_turno, ora_inizio, ora_fine)
);

CREATE TABLE IF NOT EXISTS rat (
    codice_ambulatorio VARCHAR(50),
    data_turno DATE,
    ora_inizio TIME,
    ora_fine TIME,
    PRIMARY KEY (codice_ambulatorio, data_turno, ora_inizio, ora_fine),
    FOREIGN KEY (codice_ambulatorio) REFERENCES ambulatorio(codice_ambulatorio) ON DELETE CASCADE,
    FOREIGN KEY (data_turno, ora_inizio, ora_fine) REFERENCES turno(data_turno, ora_inizio, ora_fine) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rdt (
    data_turno DATE,
    ora_inizio TIME,
    ora_fine TIME,
    cf_dipendente VARCHAR(16),
    presenza BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (data_turno, ora_inizio, ora_fine, cf_dipendente),
    FOREIGN KEY (data_turno, ora_inizio, ora_fine) REFERENCES turno(data_turno, ora_inizio, ora_fine) ON DELETE CASCADE,
    FOREIGN KEY (cf_dipendente) REFERENCES dipendente(cf) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prenotazione (
    id_prenotazione BIGINT AUTO_INCREMENT PRIMARY KEY,
    stato VARCHAR(50) NOT NULL,
    data_immissione DATE NOT NULL,
    data_prenotazione DATE NOT NULL,
    orario_prenotazione TIME NOT NULL,
    codice_ambulatorio VARCHAR(50) NOT NULL,
    cf_cliente VARCHAR(16) NOT NULL,
    cf_addetto VARCHAR(16) NOT NULL,
    saldata BOOLEAN DEFAULT FALSE,
    tipologia VARCHAR(100),
    CONSTRAINT chk_date CHECK (data_prenotazione >= data_immissione),
    UNIQUE (data_prenotazione, orario_prenotazione, codice_ambulatorio),
    FOREIGN KEY (cf_cliente) REFERENCES cliente(cf),
    FOREIGN KEY (codice_ambulatorio) REFERENCES ambulatorio(codice_ambulatorio),
    FOREIGN KEY (cf_addetto) REFERENCES dipendente(cf)
);

CREATE TABLE IF NOT EXISTS ente_assicurativo (
    piva_ente VARCHAR(20) PRIMARY KEY,
    ragione_sociale VARCHAR(255) NOT NULL,
    indirizzo VARCHAR(255),
    recapito VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS ricevuta (
    id_ricevuta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_prenotazione BIGINT NOT NULL,
    data_emissione DATE NOT NULL,
    importo_totale DECIMAL(10, 2) NOT NULL,
    quota_cliente DECIMAL(10, 2) NOT NULL,
    quota_assicurazione DECIMAL(10, 2) NOT NULL,
    piva_ente VARCHAR(20),
    CONSTRAINT chk_importo CHECK (importo_totale = quota_cliente + quota_assicurazione),
    CONSTRAINT chk_assicurazione CHECK (quota_assicurazione <= 0 OR piva_ente IS NOT NULL),
    FOREIGN KEY (id_prenotazione) REFERENCES prenotazione(id_prenotazione),
    FOREIGN KEY (piva_ente) REFERENCES ente_assicurativo(piva_ente)
);

CREATE TABLE IF NOT EXISTS cartella_clinica (
    id_cc BIGINT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS consenso (
    id_consenso BIGINT AUTO_INCREMENT PRIMARY KEY,
    cf_cliente VARCHAR(16) NOT NULL,
    id_cc BIGINT NULL,
    tipologia VARCHAR(100) NOT NULL,
    file VARCHAR(255) NOT NULL,
    FOREIGN KEY (cf_cliente) REFERENCES cliente(cf),
    FOREIGN KEY (id_cc) REFERENCES cartella_clinica(id_cc)
);

CREATE TABLE IF NOT EXISTS reclamo (
    cod_reclamo BIGINT AUTO_INCREMENT PRIMARY KEY,
    testo TEXT NOT NULL,
    cf_cliente VARCHAR(16) NOT NULL,
    cf_dipendente VARCHAR(16) NULL,
    FOREIGN KEY (cf_cliente) REFERENCES cliente(cf),
    FOREIGN KEY (cf_dipendente) REFERENCES dipendente(cf)
);

CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    data_scadenza DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    cf_utente VARCHAR(16) NOT NULL,
    data_scadenza DATETIME NOT NULL,
    revocato BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    FOREIGN KEY (cf_utente) REFERENCES utente(cf) ON DELETE CASCADE
);

-- Patch per permettere l'email vuota nei vecchi DB
-- ALTER TABLE utente MODIFY email VARCHAR(255) UNIQUE NULL;
