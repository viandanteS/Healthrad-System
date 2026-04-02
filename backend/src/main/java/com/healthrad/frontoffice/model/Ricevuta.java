package com.healthrad.frontoffice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ricevuta")
public class Ricevuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRicevuta;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prenotazione", nullable = false)
    private Prenotazione prenotazione;

    @Column(nullable = false)
    private LocalDate dataEmissione;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal importoTotale;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quotaCliente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quotaAssicurazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piva_ente")
    private EnteAssicurativo enteAssicurativo;

    public Ricevuta() {}

    public Long getIdRicevuta() { return idRicevuta; }
    public void setIdRicevuta(Long idRicevuta) { this.idRicevuta = idRicevuta; }

    public Prenotazione getPrenotazione() { return prenotazione; }
    public void setPrenotazione(Prenotazione prenotazione) { this.prenotazione = prenotazione; }

    public LocalDate getDataEmissione() { return dataEmissione; }
    public void setDataEmissione(LocalDate dataEmissione) { this.dataEmissione = dataEmissione; }

    public BigDecimal getImportoTotale() { return importoTotale; }
    public void setImportoTotale(BigDecimal importoTotale) { this.importoTotale = importoTotale; }

    public BigDecimal getQuotaCliente() { return quotaCliente; }
    public void setQuotaCliente(BigDecimal quotaCliente) { this.quotaCliente = quotaCliente; }

    public BigDecimal getQuotaAssicurazione() { return quotaAssicurazione; }
    public void setQuotaAssicurazione(BigDecimal quotaAssicurazione) { this.quotaAssicurazione = quotaAssicurazione; }

    public EnteAssicurativo getEnteAssicurativo() { return enteAssicurativo; }
    public void setEnteAssicurativo(EnteAssicurativo enteAssicurativo) { this.enteAssicurativo = enteAssicurativo; }
}
