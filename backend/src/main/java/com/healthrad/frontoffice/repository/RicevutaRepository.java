package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Ricevuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RicevutaRepository extends JpaRepository<Ricevuta, Long> {
    Optional<Ricevuta> findByPrenotazione_IdPrenotazione(Long idPrenotazione);
}
