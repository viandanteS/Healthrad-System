package com.healthrad.frontoffice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ambulatorio")
public class Ambulatorio {

    @Id
    private String codiceAmbulatorio;

    public Ambulatorio() {}

    public String getCodiceAmbulatorio() { return codiceAmbulatorio; }
    public void setCodiceAmbulatorio(String codiceAmbulatorio) { this.codiceAmbulatorio = codiceAmbulatorio; }
}
