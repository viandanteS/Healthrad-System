package com.healthrad.frontoffice.service;

import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.model.Ricevuta;
import com.healthrad.frontoffice.repository.PrenotazioneRepository;
import com.healthrad.frontoffice.repository.RicevutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RicevutaService {

    @Autowired
    private RicevutaRepository ricevutaRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Transactional
    public Ricevuta elaboraTransazione(Ricevuta ricevuta) {
        // Vincolo DB e Business: Split payment
        BigDecimal totale = ricevuta.getQuotaCliente().add(ricevuta.getQuotaAssicurazione());

        if (ricevuta.getImportoTotale().compareTo(totale) != 0) {
            throw new IllegalArgumentException("Errore Split Payment: L'importo totale differisce dalla somma delle quote.");
        }

        if (ricevuta.getQuotaAssicurazione().compareTo(BigDecimal.ZERO) > 0 && ricevuta.getEnteAssicurativo() == null) {
            throw new IllegalArgumentException("Per le prestazioni parzialmente o totalmente coperte da assicurazione e' necessario specificare la P.IVA dell'Ente.");
        }

        // Cerca prenotazione per aggiornare lo status di 'saldata'
        Prenotazione p = prenotazioneRepository.findById(ricevuta.getPrenotazione().getIdPrenotazione())
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        p.setSaldata(true);
        prenotazioneRepository.save(p);

        // Associa la prenotazione persistita per il cascade/relazione corretta e salva la ricevuta
        ricevuta.setPrenotazione(p);
        
        return ricevutaRepository.save(ricevuta);
    }
}
