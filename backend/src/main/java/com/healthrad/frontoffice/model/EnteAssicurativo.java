package com.healthrad.frontoffice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "ente_assicurativo")
public class EnteAssicurativo {

    @Id
    @Column(length = 20)
    private String pivaEnte;

    @Column(nullable = false)
    private String ragioneSociale;

    private String indirizzo;

    private String recapito;

    public EnteAssicurativo() {}

    public String getPivaEnte() { return pivaEnte; }
    public void setPivaEnte(String pivaEnte) { this.pivaEnte = pivaEnte; }

    public String getRagioneSociale() { return ragioneSociale; }
    public void setRagioneSociale(String ragioneSociale) { this.ragioneSociale = ragioneSociale; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public String getRecapito() { return recapito; }
    public void setRecapito(String recapito) { this.recapito = recapito; }
}
