package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    // Ricerca per nome O cognome O email (case-insensitive)
    List<Cliente> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String nome, String cognome, String email
    );
}
