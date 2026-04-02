package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Ricevuta;
import com.healthrad.frontoffice.service.RicevutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ricevute")
public class RicevutaController {

    @Autowired
    private RicevutaService ricevutaService;

    @PostMapping
    public ResponseEntity<?> elaboraTransazione(@RequestBody Ricevuta ricevuta) {
        try {
            Ricevuta processed = ricevutaService.elaboraTransazione(ricevuta);
            return ResponseEntity.ok(processed);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
