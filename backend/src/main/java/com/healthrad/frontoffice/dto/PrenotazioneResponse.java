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
    private String operatore;

    private Boolean saldata;
    private String tipologia;

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

        if (p.getMedico() != null) {
            this.cfMedico = p.getMedico().getCf();
            this.nomeMedico = p.getMedico().getNome() + " " + p.getMedico().getCognome();
            this.operatore = this.nomeMedico;
        }
    }


    public Long getId() {
        return id;
    }

    public String getStato() {
        return stato;
    }

    public LocalDate getDataImmissione() {
        return dataImmissione;
    }

    public LocalDate getDataPrenotazione() {
        return dataPrenotazione;
    }

    public LocalTime getOrarioPrenotazione() {
        return orarioPrenotazione;
    }

    public String getCodiceAmbulatorio() {
        return codiceAmbulatorio;
    }

    public String getCfCliente() {
        return cfCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public String getCfMedico() {
        return cfMedico;
    }

    public String getOperatore() {
        return operatore;
    }

    public String getNomeMedico() {
        return nomeMedico;
    }

    public Boolean getSaldata() {
        return saldata;
    }

    public String getTipologia() {
        return tipologia;
    }
}
