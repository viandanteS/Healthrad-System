package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente, String> {
}
