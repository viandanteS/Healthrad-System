package com.healthrad.frontoffice.dto;

import com.healthrad.frontoffice.model.Prenotazione;

import java.time.LocalDate;
import java.time.LocalTime;

public class PrenotazioneResponse {

    private Long id;
    private String stato;
    private LocalDate dataImmissione;
    private LocalDate dataPrenotazione;
    private LocalTime orarioPrenotazione;
    
    private String codiceAmbulatorio;
    private String cfCliente;
    private String nomeCliente;
    private String cognomeCliente;
    
    private String cfMedico;
    private String nomeMedico;

    private Boolean saldata;
    private String tipologia;

    // Costruttore dal modello
    public PrenotazioneResponse(Prenotazione p) {
        this.id = p.getIdPrenotazione();
        this.stato = p.getStato();
        this.dataImmissione = p.getDataImmissione();
        this.dataPrenotazione = p.getDataPrenotazione();
        this.orarioPrenotazione = p.getOrarioPrenotazione();
        this.saldata = p.getSaldata();
        this.tipologia = p.getTipologia();
        
        if (p.getAmbulatorio() != null) {
            this.codiceAmbulatorio = p.getAmbulatorio().getCodiceAmbulatorio();
        }
        
        if (p.getCliente() != null) {
            this.cfCliente = p.getCliente().getCf();
            this.nomeCliente = p.getCliente().getNome();
            this.cognomeCliente = p.getCliente().getCognome();
        }

        // Il medico di un appuntamento è l'addetto?
        // Supponiamo che l'addetto alla prenotazione sia il medico o il tecnico
        if (p.getAddetto() != null) {
            this.cfMedico = p.getAddetto().getCf();
            this.nomeMedico = p.getAddetto().getRuolo(); // Placeholder: potremmo mettere il ruolo o legarci all'entità Utente per il nome
        }
    }

    public Long getId() { return id; }
    public String getStato() { return stato; }
    public LocalDate getDataImmissione() { return dataImmissione; }
    public LocalDate getDataPrenotazione() { return dataPrenotazione; }
    public LocalTime getOrarioPrenotazione() { return orarioPrenotazione; }
    public String getCodiceAmbulatorio() { return codiceAmbulatorio; }
    public String getCfCliente() { return cfCliente; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCognomeCliente() { return cognomeCliente; }
    public String getCfMedico() { return cfMedico; }
    public String getNomeMedico() { return nomeMedico; }
    public Boolean getSaldata() { return saldata; }
    public String getTipologia() { return tipologia; }
}
