package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.service.ConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/consensi")
@CrossOrigin(origins = "http://localhost:4200")
public class ConsensoController {

    @Autowired
    private ConsensoService consensoService;

    @PostMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> creaConsenso(
            @RequestParam("cfCliente") String cfCliente,
            @RequestParam("tipologia") String tipologia,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Nessun file selezionato");
            }
            // Salvataggio Blob su database
            consensoService.allegaConsenso(cfCliente, tipologia, file);
            return ResponseEntity.ok(Map.of("message", "Consenso inserito con successo"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
