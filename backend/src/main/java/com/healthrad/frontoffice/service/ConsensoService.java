package com.healthrad.frontoffice.service;

import com.healthrad.frontoffice.model.Cliente;
import com.healthrad.frontoffice.model.Consenso;
import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.repository.ClienteRepository;
import com.healthrad.frontoffice.repository.ConsensoRepository;
import com.healthrad.frontoffice.repository.PrenotazioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ConsensoService {

    @Autowired
    private ConsensoRepository consensoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Transactional
    public Consenso allegaConsenso(String cfCliente, Long idPrenotazione, String tipologia, MultipartFile filePdf) throws IOException {
        Cliente cliente = clienteRepository.findById(cfCliente)
            .orElseThrow(() -> new IllegalArgumentException("Cliente non trovato"));

        Prenotazione prenotazione = prenotazioneRepository.findById(idPrenotazione)
            .orElseThrow(() -> new IllegalArgumentException("Prenotazione non trovata"));

        // Verifica se esiste già un consenso per questa prenotazione
        if (consensoRepository.findByPrenotazione_IdPrenotazione(idPrenotazione).isPresent()) {
            throw new IllegalStateException("Un consenso e gia stato caricato per questa prenotazione");
        }

        Consenso consenso = new Consenso();
        consenso.setCliente(cliente);
        consenso.setPrenotazione(prenotazione);
        consenso.setTipologia(tipologia);
        consenso.setNomeFile(filePdf.getOriginalFilename());
        consenso.setFile(filePdf.getBytes());

        return consensoRepository.save(consenso);
    }

    public Consenso getConsenso(Long id) {
        return consensoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Consenso non trovato"));
    }

    public Consenso getByPrenotazione(Long idPrenotazione) {
        return consensoRepository.findByPrenotazione_IdPrenotazione(idPrenotazione)
            .orElseThrow(() -> new IllegalArgumentException("Nessun consenso trovato per questa prenotazione"));
    }

    @Transactional
    public void eliminaConsenso(Long id) {
        if (!consensoRepository.existsById(id)) {
            throw new IllegalArgumentException("Consenso non trovato");
        }
        consensoRepository.deleteById(id);
    }
}
