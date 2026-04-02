package com.healthrad.frontoffice.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "utente")
@Inheritance(strategy = InheritanceType.JOINED)
public class Utente {

    @Id
    @Column(length = 16)
    private String cf;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(name = "data_nascita", nullable = false)
    private LocalDate dataNascita;

    private String telefono;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Costruttori
    public Utente() {}

    // Getters e Setters
    public String getCf() { return cf; }
    public void setCf(String cf) { this.cf = cf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
