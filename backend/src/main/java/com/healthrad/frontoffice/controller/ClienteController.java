package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Cliente;
import com.healthrad.frontoffice.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/clienti")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * GET /api/clienti?q=... — Restituisce tutti i clienti, con ricerca opzionale.
     * Usata dall'Anagrafica per popolare la tabella.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public List<Cliente> getClienti(@RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            return clienteRepository
                .findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, q);
        }
        return clienteRepository.findAll();
    }

    @GetMapping("/cerca")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<Cliente> ricercaCliente(@RequestParam("cf") String cf) {
        return clienteRepository.findById(cf)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Autowired
    private com.healthrad.frontoffice.service.EmailService emailService;

    // Metodo helper per generazione token/password random e validazione email
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private String generateRandomPassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * POST /api/clienti — Crea un nuovo profilo cliente.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> creaCliente(@RequestBody Cliente cliente) {
        if (cliente.getCf() == null || cliente.getCf().length() != 16) {
            return ResponseEntity.badRequest().body("Il Codice Fiscale deve essere di esattamente 16 caratteri.");
        }
        if (clienteRepository.existsById(cliente.getCf())) {
            return ResponseEntity.badRequest().body("Esiste già un cliente con questo Codice Fiscale.");
        }
        
        if (cliente.getDataNascita() == null) {
            return ResponseEntity.badRequest().body("La data di nascita è obbligatoria.");
        }
        if (cliente.getDataNascita().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest().body("La data di nascita non può essere nel futuro.");
        }
        
        // Gestione email facoltativa e invio credenziali
        if (cliente.getEmail() != null && !cliente.getEmail().isBlank()) {
            if (!isValidEmail(cliente.getEmail())) {
                return ResponseEntity.badRequest().body("Il formato dell'email non è valido.");
            }
            // Genera password, assega l'hash e invia email
            String rawPassword = generateRandomPassword();
            cliente.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt(10)));
            emailService.inviaCredenziali(cliente.getEmail(), rawPassword);
        } else {
            cliente.setEmail(null);
            cliente.setPassword("[non_impostata]");
        }

        Cliente saved = clienteRepository.save(cliente);
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /api/clienti/{cf} — Aggiorna un profilo cliente esistente.
     */
    @PutMapping("/{cf}")
    @PreAuthorize("hasAuthority('Addetto al Front-Office')")
    public ResponseEntity<?> aggiornaCliente(@PathVariable String cf, @RequestBody Cliente clienteAggiornato) {
        if (clienteAggiornato.getDataNascita() == null) {
            return ResponseEntity.badRequest().body("La data di nascita è obbligatoria.");
        }
        if (clienteAggiornato.getDataNascita().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest().body("La data di nascita non può essere nel futuro.");
        }
        
        return clienteRepository.findById(cf).map(clienteEsistente -> {
            
            String oldEmail = clienteEsistente.getEmail();
            String newEmail = clienteAggiornato.getEmail();

            // Se l'email viene inserita per la prima volta
            boolean firstTimeEmail = (oldEmail == null || oldEmail.isBlank()) && (newEmail != null && !newEmail.isBlank());

            clienteEsistente.setNome(clienteAggiornato.getNome());
            clienteEsistente.setCognome(clienteAggiornato.getCognome());
            
            if (newEmail != null && !newEmail.isBlank()) {
                if (!isValidEmail(newEmail)) {
                    throw new IllegalArgumentException("Il formato dell'email non è valido.");
                }
                clienteEsistente.setEmail(newEmail);
                
                if (firstTimeEmail) {
                    // Genera credenziali al volo per chi ha appena inserito la mail
                    String rawPassword = generateRandomPassword();
                    clienteEsistente.setPassword(org.mindrot.jbcrypt.BCrypt.hashpw(rawPassword, org.mindrot.jbcrypt.BCrypt.gensalt(10)));
                    emailService.inviaCredenziali(newEmail, rawPassword);
                }
            } else {
                clienteEsistente.setEmail(null);
            }

            clienteEsistente.setTelefono(clienteAggiornato.getTelefono());
            clienteEsistente.setDataNascita(clienteAggiornato.getDataNascita());
            
            Cliente salvato = clienteRepository.save(clienteEsistente);
            return ResponseEntity.ok().body(salvato);
        }).orElse(ResponseEntity.notFound().build());
    }
}
