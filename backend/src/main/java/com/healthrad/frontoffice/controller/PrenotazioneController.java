package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Prenotazione;
import com.healthrad.frontoffice.service.PrenotazioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import com.healthrad.frontoffice.dto.PrenotazioneResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prenotazioni")
public class PrenotazioneController {

    @Autowired
    private PrenotazioneService prenotazioneService;

    @GetMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<List<PrenotazioneResponse>> getAllPrenotazioni() {
        List<PrenotazioneResponse> response = prenotazioneService.getAllPrenotazioni()
                .stream()
                .map(PrenotazioneResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponibilita")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<List<LocalTime>> getOrariOccupati(
            @RequestParam("data") String data, 
            @RequestParam("ambulatorio") String ambulatorio) {
        LocalDate parsedData = LocalDate.parse(data);
        return ResponseEntity.ok(prenotazioneService.getOrariOccupati(parsedData, ambulatorio));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> aggiungiNuovaPrenotazione(@RequestBody Prenotazione prenotazione) {
        try {
            Prenotazione saved = prenotazioneService.creaPrenotazione(prenotazione);
            return ResponseEntity.ok(new PrenotazioneResponse(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/accetta")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> accettaCliente(@PathVariable Long id) {
        try {
            Prenotazione accepted = prenotazioneService.accettaCliente(id);
            return ResponseEntity.ok(new PrenotazioneResponse(accepted));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/salda")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> saldaPrenotazione(@PathVariable Long id) {
        try {
            Prenotazione saldata = prenotazioneService.saldaPrenotazione(id);
            return ResponseEntity.ok(new PrenotazioneResponse(saldata));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> cancellaPrenotazione(@PathVariable Long id) {
        try {
            prenotazioneService.cancellaPrenotazione(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
