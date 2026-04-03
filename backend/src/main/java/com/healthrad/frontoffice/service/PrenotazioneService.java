package com.healthrad.frontoffice.service;

import com.healthrad.frontoffice.model.Dipendente;
import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.repository.DipendenteRepository;
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

    @Autowired
    private DipendenteRepository dipendenteRepository;

    private static final int DURATA_PRENOTAZIONE_MINUTI = 30;

    public List<Prenotazione> getAllPrenotazioni() {
        return prenotazioneRepository.findAll();
    }

    @Transactional
    public Prenotazione creaPrenotazione(Prenotazione p) {
        // Inizializza i campi obbligatori o di default non inviati dal frontend
        p.setDataImmissione(LocalDate.now());
        if (p.getSaldata() == null) p.setSaldata(false);

        // 1. Recupera il CF del medico dal repository (query nativa sicura)
        String cfMedico = dipendenteRepository.findCfMedicoByAmbulatorioAndDataAndOra(
                p.getAmbulatorio().getCodiceAmbulatorio(),
                p.getDataPrenotazione(),
                p.getOrarioPrenotazione()
        ).orElseThrow(() -> new IllegalArgumentException(
                "Nessun medico disponibile in " + p.getAmbulatorio().getCodiceAmbulatorio() +
                " per il giorno " + p.getDataPrenotazione() + " alle " + p.getOrarioPrenotazione()
        ));

        // 2. Recupera l'oggetto Dipendente completo tramite JPA (gestisce correttamente JOINED inheritance)
        Dipendente medico = dipendenteRepository.findById(cfMedico)
                .orElseThrow(() -> new IllegalStateException("Medico con CF " + cfMedico + " non trovato nell'anagrafica"));

        p.setMedico(medico);

        // 3. Controllo overlap
        List<LocalTime> occupati = getOrariOccupati(p.getDataPrenotazione(), p.getAmbulatorio().getCodiceAmbulatorio());
        if (occupati.contains(p.getOrarioPrenotazione())) {
            throw new IllegalArgumentException("Orario già occupato");
        }

        return prenotazioneRepository.save(p);
    }

    public List<LocalTime> getOrariOccupati(LocalDate data, String codiceAmbulatorio) {
        List<Prenotazione> prenotazioni = prenotazioneRepository.findByDataPrenotazioneAndAmbulatorio_CodiceAmbulatorio(data, codiceAmbulatorio);
        return prenotazioni.stream().map(Prenotazione::getOrarioPrenotazione).toList();
    }

    public List<LocalTime> getOrariDisponibili(LocalDate data, String codiceAmbulatorio) {
        List<Object[]> turni = dipendenteRepository.findTurniByAmbulatorioAndData(codiceAmbulatorio, data);
        List<LocalTime> occupati = getOrariOccupati(data, codiceAmbulatorio);
        
        java.util.ArrayList<LocalTime> disponibili = new java.util.ArrayList<>();
        LocalTime current = LocalTime.of(8, 0);
        LocalTime fineGiornata = LocalTime.of(20, 0);
        
        while (current.isBefore(fineGiornata)) {
            final LocalTime slot = current;
            boolean copertoDaTurno = turni.stream().anyMatch(t -> {
                LocalTime inizioTurno = (LocalTime) t[0];
                LocalTime fineTurno = (LocalTime) t[1];
                return (slot.equals(inizioTurno) || slot.isAfter(inizioTurno)) && 
                       (slot.plusMinutes(DURATA_PRENOTAZIONE_MINUTI).isBefore(fineTurno) || 
                        slot.plusMinutes(DURATA_PRENOTAZIONE_MINUTI).equals(fineTurno));
            });
            
            if (copertoDaTurno && !occupati.contains(slot)) {
                disponibili.add(slot);
            }
            current = current.plusMinutes(DURATA_PRENOTAZIONE_MINUTI);
        }
        return disponibili;
    }

    public List<String> getAmbulatoriPerData(LocalDate data) {
        return dipendenteRepository.findAmbulatoriByData(data);
    }

    @Transactional
    public Prenotazione modificaPrenotazione(Long idPrenotazione, Prenotazione datiAggiornati) {
        Prenotazione p = prenotazioneRepository.findById(idPrenotazione)
                .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        p.setTipologia(datiAggiornati.getTipologia());
        p.setAmbulatorio(datiAggiornati.getAmbulatorio());
        p.setDataPrenotazione(datiAggiornati.getDataPrenotazione());
        p.setOrarioPrenotazione(datiAggiornati.getOrarioPrenotazione());

        String cfMedico = dipendenteRepository.findCfMedicoByAmbulatorioAndDataAndOra(
                p.getAmbulatorio().getCodiceAmbulatorio(),
                p.getDataPrenotazione(),
                p.getOrarioPrenotazione()
        ).orElseThrow(() -> new IllegalArgumentException("Nessun medico disponibile per i nuovi dati"));

        Dipendente medico = dipendenteRepository.findById(cfMedico).orElseThrow();
        p.setMedico(medico);

        return prenotazioneRepository.save(p);
    }

    @Transactional
    public Prenotazione accettaCliente(Long id) {
        Prenotazione p = prenotazioneRepository.findById(id).orElseThrow();
        p.setStato("In Corso");
        return prenotazioneRepository.save(p);
    }

    @Transactional
    public Prenotazione saldaPrenotazione(Long id) {
        Prenotazione p = prenotazioneRepository.findById(id).orElseThrow();
        p.setSaldata(true);
        return prenotazioneRepository.save(p);
    }

    @Transactional
    public void cancellaPrenotazione(Long id) {
        Prenotazione p = prenotazioneRepository.findById(id).orElseThrow();
        prenotazioneRepository.delete(p);
    }
}
