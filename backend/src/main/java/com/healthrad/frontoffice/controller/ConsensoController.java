package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Consenso;
import com.healthrad.frontoffice.service.ConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
            @RequestParam("idPrenotazione") Long idPrenotazione,
            @RequestParam("tipologia") String tipologia,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Nessun file selezionato");
            }
            // Salvataggio Blob su database
            consensoService.allegaConsenso(cfCliente, idPrenotazione, tipologia, file);
            return ResponseEntity.ok(Map.of("message", "Consenso inserito con successo"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<byte[]> downloadConsenso(@PathVariable Long id) {
        try {
            Consenso consenso = consensoService.getConsenso(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + consenso.getNomeFile() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(consenso.getFile());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/prenotazione/{id}")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> getConsensoByPrenotazione(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(consensoService.getByPrenotazione(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> eliminaConsenso(@PathVariable Long id) {
        try {
            consensoService.eliminaConsenso(id);
            return ResponseEntity.ok(Map.of("message", "Consenso eliminato con successo"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
