package com.healthrad.frontoffice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dipendente")
public class Dipendente extends Utente {

    @Column(nullable = false)
    private String ruolo;

    public Dipendente() {
        super();
    }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}
