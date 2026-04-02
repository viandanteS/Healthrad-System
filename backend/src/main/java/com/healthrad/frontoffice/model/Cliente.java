package com.healthrad.frontoffice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente extends Utente {

    // Cliente eredita tutti i campi anagrafici e la chiave "cf" da Utente, 
    // creando un record associato per l'accesso differenziato nel sistema

    public Cliente() {
        super();
    }
}
