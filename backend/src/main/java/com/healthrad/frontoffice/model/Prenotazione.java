package com.healthrad.frontoffice.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "prenotazione")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrenotazione;

    @Column(nullable = false)
    private String stato;

    @Column(nullable = false)
    private LocalDate dataImmissione;

    @Column(nullable = false)
    private LocalDate dataPrenotazione;

    @Column(nullable = false)
    private LocalTime orarioPrenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codice_ambulatorio", nullable = false)
    private Ambulatorio ambulatorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cf_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cf_addetto", nullable = false)
    private Dipendente addetto;

    private Boolean saldata = false;

    private String tipologia;

    public Prenotazione() {}

    // Getters and Setters

    public Long getIdPrenotazione() { return idPrenotazione; }
    public void setIdPrenotazione(Long idPrenotazione) { this.idPrenotazione = idPrenotazione; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public LocalDate getDataImmissione() { return dataImmissione; }
    public void setDataImmissione(LocalDate dataImmissione) { this.dataImmissione = dataImmissione; }

    public LocalDate getDataPrenotazione() { return dataPrenotazione; }
    public void setDataPrenotazione(LocalDate dataPrenotazione) { this.dataPrenotazione = dataPrenotazione; }

    public LocalTime getOrarioPrenotazione() { return orarioPrenotazione; }
    public void setOrarioPrenotazione(LocalTime orarioPrenotazione) { this.orarioPrenotazione = orarioPrenotazione; }

    public Ambulatorio getAmbulatorio() { return ambulatorio; }
    public void setAmbulatorio(Ambulatorio ambulatorio) { this.ambulatorio = ambulatorio; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Dipendente getAddetto() { return addetto; }
    public void setAddetto(Dipendente addetto) { this.addetto = addetto; }

    public Boolean getSaldata() { return saldata; }
    public void setSaldata(Boolean saldata) { this.saldata = saldata; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }
}
