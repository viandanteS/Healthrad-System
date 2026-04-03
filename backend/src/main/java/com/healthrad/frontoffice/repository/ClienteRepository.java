package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Cliente c WHERE LOWER(c.cf) LIKE LOWER(CONCAT('%', :txt, '%')) OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :txt, '%')) OR LOWER(c.cognome) LIKE LOWER(CONCAT('%', :txt, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :txt, '%'))")
    List<Cliente> cercaClienti(
        @org.springframework.data.repository.query.Param("txt") String txt
    );
}
