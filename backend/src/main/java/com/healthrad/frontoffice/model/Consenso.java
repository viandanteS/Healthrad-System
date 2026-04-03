package com.healthrad.frontoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consenso")
public class Consenso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsenso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cf_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cc")
    private CartellaClinica cartellaClinica;

    @Column(nullable = false, length = 100)
    private String tipologia;

    @Lob
    @Column(nullable = false)
    private byte[] file;

    @Column(name = "nome_file")
    private String nomeFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prenotazione")
    private Prenotazione prenotazione;

    public Consenso() {}

    public Long getIdConsenso() {
        return idConsenso;
    }

    public void setIdConsenso(Long idConsenso) {
        this.idConsenso = idConsenso;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public CartellaClinica getCartellaClinica() {
        return cartellaClinica;
    }

    public void setCartellaClinica(CartellaClinica cartellaClinica) {
        this.cartellaClinica = cartellaClinica;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }

    public void setPrenotazione(Prenotazione prenotazione) {
        this.prenotazione = prenotazione;
    }
}
