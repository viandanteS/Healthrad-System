package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Consenso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsensoRepository extends JpaRepository<Consenso, Long> {
    Optional<Consenso> findByPrenotazione_IdPrenotazione(Long idPrenotazione);
    List<Consenso> findByCliente_Cf(String cf);
}
