package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Prenotazione;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    
    @Override
    @EntityGraph(attributePaths = {"cliente", "ambulatorio", "medico"})
    List<Prenotazione> findAll();

    @Override
    @EntityGraph(attributePaths = {"cliente", "ambulatorio", "medico"})
    Optional<Prenotazione> findById(Long id);

    // Metodo per trovare le prenotazioni in un determinato ambulatorio e data (utile per controllo overlap)
    List<Prenotazione> findByDataPrenotazioneAndAmbulatorio_CodiceAmbulatorio(LocalDate dataPrenotazione, String codiceAmbulatorio);
}

