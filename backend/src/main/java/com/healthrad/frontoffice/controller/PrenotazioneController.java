package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @PostMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> aggiungiNuovaPrenotazione(@RequestBody Prenotazione prenotazione) {
        try {
            Prenotazione saved = prenotazioneService.creaPrenotazione(prenotazione);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/accetta")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> accettaCliente(@PathVariable Long id) {
        try {
            Prenotazione accepted = prenotazioneService.accettaCliente(id);
            return ResponseEntity.ok(accepted);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
