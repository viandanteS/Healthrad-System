package com.healthrad.frontoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cartella_clinica")
public class CartellaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCc;

    public CartellaClinica() {}

    public Long getIdCc() {
        return idCc;
    }

    public void setIdCc(Long idCc) {
        this.idCc = idCc;
    }
}
