package com.healthrad.frontoffice.service;

import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    private static final int DURATA_PRENOTAZIONE_MINUTI = 30;

    @Transactional
    public Prenotazione creaPrenotazione(Prenotazione p) {
        // Vincolo 1: Forziamo stato iniziale
        p.setStato("Prenotato");
        p.setDataImmissione(LocalDate.now());
        
        // Validazione Sovrapposizione
        List<Prenotazione> asseganzioniOdierne = prenotazioneRepository.findByDataPrenotazioneAndAmbulatorio_CodiceAmbulatorio(
            p.getDataPrenotazione(), p.getAmbulatorio().getCodiceAmbulatorio()
        );

        LocalTime nuovoInizio = p.getOrarioPrenotazione();
        LocalTime nuovaFine = nuovoInizio.plusMinutes(DURATA_PRENOTAZIONE_MINUTI);

        for (Prenotazione ex : asseganzioniOdierne) {
            LocalTime exInizio = ex.getOrarioPrenotazione();
            LocalTime exFine = exInizio.plusMinutes(DURATA_PRENOTAZIONE_MINUTI);

            // Controllo overlap temporale
            if (nuovoInizio.isBefore(exFine) && nuovaFine.isAfter(exInizio)) {
                throw new IllegalArgumentException("Sovrapposizione orario: L'ambulatorio è già occupato per questo slot.");
            }
        }

        return prenotazioneRepository.save(p);
    }

    @Transactional
    public Prenotazione accettaCliente(Long idPrenotazione) {
        Prenotazione p = prenotazioneRepository.findById(idPrenotazione)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
            
        if (!"Prenotato".equalsIgnoreCase(p.getStato())) {
            throw new IllegalStateException("Solo le prenotazioni in stato 'Prenotato' possono essere accettate.");
        }
        
        p.setStato("In attesa");
        return prenotazioneRepository.save(p);
    }

    public List<Prenotazione> getAllPrenotazioni() {
        return prenotazioneRepository.findAll();
    }

    @Transactional
    public Prenotazione saldaPrenotazione(Long idPrenotazione) {
        Prenotazione p = prenotazioneRepository.findById(idPrenotazione)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
        p.setSaldata(true);
        return prenotazioneRepository.save(p);
    }
    
    @Transactional
    public void cancellaPrenotazione(Long idPrenotazione) {
        Prenotazione p = prenotazioneRepository.findById(idPrenotazione)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));
        if (!"Prenotato".equalsIgnoreCase(p.getStato())) {
            throw new IllegalStateException("Puoi cancellare solo appuntamenti in stato 'Prenotato'.");
        }
        prenotazioneRepository.delete(p);
    }
    
    public List<LocalTime> getOrariOccupati(LocalDate data, String codiceAmbulatorio) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataPrenotazioneAndAmbulatorio_CodiceAmbulatorio(data, codiceAmbulatorio);
        return prenotazioni.stream().map(Prenotazione::getOrarioPrenotazione).toList();
    }
}
