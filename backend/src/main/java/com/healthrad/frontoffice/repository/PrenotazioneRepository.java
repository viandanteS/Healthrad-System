package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {
    
    // Metodo per trovare le prenotazioni in un determinato ambulatorio e data (utile per controllo overlap)
    List<Prenotazione> findByDataPrenotazioneAndAmbulatorio_CodiceAmbulatorio(LocalDate dataPrenotazione, String codiceAmbulatorio);
}
