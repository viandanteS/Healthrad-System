package com.healthrad.frontoffice.controller;

import com.healthrad.frontoffice.model.Utente;
import com.healthrad.frontoffice.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Permette ad Angular di fare richieste
public class AuthController {

    @Autowired
    private UtenteRepository utenteRepository;

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @Autowired
    private com.healthrad.frontoffice.security.JwtUtil jwtUtil;

    @Autowired
    private com.healthrad.frontoffice.repository.DipendenteRepository dipendenteRepository;

    @Autowired
    private com.healthrad.frontoffice.repository.ClienteRepository clienteRepository;

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginRequest req) {
        
        Optional<Utente> usrOpt = utenteRepository.findByEmail(req.email);
        
        if (usrOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide.");
        }

        Utente u = usrOpt.get();
        boolean pwOk = BCrypt.checkpw(req.password, u.getPassword());

        if (!pwOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali non valide.");
        }

        // Determina il ruolo dell'utente in base al tipo di entità nel DB
        String role;
        var dipOpt = dipendenteRepository.findById(u.getCf());
        var cliOpt = clienteRepository.findById(u.getCf());

        if (dipOpt.isPresent()) {
            // Es: "Addetto al Front-Office", "Medico specialista", etc.
            role = dipOpt.get().getRuolo();
        } else if (cliOpt.isPresent()) {
            role = "CLIENTE";
        } else {
            role = "UNKNOWN";
        }

        // Token JWT con ruolo embeddato nel claim "role"
        String realToken = jwtUtil.generateToken(u.getEmail(), role);

        Map<String, String> creds = new HashMap<>();
        creds.put("access_token", realToken);
        creds.put("utente", u.getNome() + " " + u.getCognome());
        creds.put("role", role);
        return ResponseEntity.ok(creds);
    }

    // Endpoint di debug - SOLO per sviluppo, da rimuovere in produzione
    @GetMapping("/debug/utenti")
    public ResponseEntity<?> debugUtenti() {
        long count = utenteRepository.count();
        var emails = utenteRepository.findAll().stream()
            .map(u -> u.getEmail() + " (hash: " + u.getPassword().substring(0, 15) + "...)")
            .toList();
        return ResponseEntity.ok(java.util.Map.of("count", count, "utenti", emails));
    }

    /**
     * Rigenera gli hash BCrypt di tutti gli utenti con la password "password".
     * Usare SOLO in sviluppo per correggere hash generati da altri strumenti.
     */
    @PostMapping("/debug/fix-passwords")
    public ResponseEntity<?> fixPasswords() {
        var utenti = utenteRepository.findAll();
        String nuovoHash = BCrypt.hashpw("password", BCrypt.gensalt(10));
        utenti.forEach(u -> u.setPassword(nuovoHash));
        utenteRepository.saveAll(utenti);
        return ResponseEntity.ok("Password aggiornate per " + utenti.size() + " utenti. Riprova il login.");
    }
}
