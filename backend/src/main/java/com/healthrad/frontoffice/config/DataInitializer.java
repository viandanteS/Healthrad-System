package com.healthrad.frontoffice.config;

import com.healthrad.frontoffice.model.Cliente;
import com.healthrad.frontoffice.model.Dipendente;

import com.healthrad.frontoffice.repository.ClienteRepository;
import com.healthrad.frontoffice.repository.DipendenteRepository;
import com.healthrad.frontoffice.repository.UtenteRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UtenteRepository utenteRepository;
    @Autowired private DipendenteRepository dipendenteRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private static final String TEST_PASSWORD = "password";

    @Override
    public void run(ApplicationArguments args) {

        String hash = BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt(10));
        log.info("DataInitializer: sincronizzazione utenti con hash BCrypt aggiornato...");

        // ---- 1. Addetto al Front-Office ----
        Dipendente fo = (Dipendente) utenteRepository.findById("FO00000000000001")
            .orElse(new Dipendente());
        fo.setCf("FO00000000000001");
        fo.setNome("Mario");
        fo.setCognome("Rossi");
        fo.setDataNascita(LocalDate.of(1985, 5, 20));
        fo.setTelefono("+39 347 123 4567");
        fo.setEmail("frontoffice@healthrad.it");
        fo.setPassword(hash);
        fo.setRuolo("Addetto al Front-Office");
        dipendenteRepository.save(fo);
        log.info("Sincronizzato: frontoffice@healthrad.it con nuovo hash");

        // ---- 2. Medico Specialista 1 (Giulia Bianchi) ----
        Dipendente medico1 = (Dipendente) utenteRepository.findById("MED0000000000001")
            .orElse(new Dipendente());
        medico1.setCf("MED0000000000001");
        medico1.setNome("Giulia");
        medico1.setCognome("Bianchi");
        medico1.setDataNascita(LocalDate.of(1970, 11, 12));
        medico1.setTelefono("+39 333 987 6543");
        medico1.setEmail("medico1@healthrad.it");
        medico1.setPassword(hash);
        medico1.setRuolo("Medico specialista");
        dipendenteRepository.save(medico1);
        log.info("Sincronizzato: medico1@healthrad.it");

        // ---- 2b. Medico Specialista 2 (Marco Neri) ----
        Dipendente medico2 = (Dipendente) utenteRepository.findById("MED0000000000002")
            .orElse(new Dipendente());
        medico2.setCf("MED0000000000002");
        medico2.setNome("Marco");
        medico2.setCognome("Neri");
        medico2.setDataNascita(LocalDate.of(1980, 3, 15));
        medico2.setTelefono("+39 340 111 2222");
        medico2.setEmail("medico2@healthrad.it");
        medico2.setPassword(hash);
        medico2.setRuolo("Medico specialista");
        dipendenteRepository.save(medico2);
        log.info("Sincronizzato: medico2@healthrad.it");

        // ---- 2c. Medico Specialista 3 (Elena Verdi) ----
        Dipendente medico3 = (Dipendente) utenteRepository.findById("MED0000000000003")
            .orElse(new Dipendente());
        medico3.setCf("MED0000000000003");
        medico3.setNome("Elena");
        medico3.setCognome("Verdi");
        medico3.setDataNascita(LocalDate.of(1988, 7, 24));
        medico3.setTelefono("+39 349 333 4444");
        medico3.setEmail("medico3@healthrad.it");
        medico3.setPassword(hash);
        medico3.setRuolo("Medico specialista");
        dipendenteRepository.save(medico3);
        log.info("Sincronizzato: medico3@healthrad.it");

        // ---- 3. Cliente ----
        Cliente cliente = (Cliente) utenteRepository.findById("CLI0000000000001")
            .orElse(new Cliente());
        cliente.setCf("CLI0000000000001");
        cliente.setNome("Luca");
        cliente.setCognome("Verdi");
        cliente.setDataNascita(LocalDate.of(1995, 2, 28));
        cliente.setTelefono("+39 320 541 2368");
        cliente.setEmail("cliente@example.com");
        cliente.setPassword(hash);
        clienteRepository.save(cliente);
        log.info("Sincronizzato: cliente@example.com");

        // ---- 4. Turni, RAT e RDT Sincronizzati ----
        log.info("DataInitializer: inserimento turni e associazioni RAT/RDT diversificate...");
        
        // Medico 1 (Giulia): Lunedì e Mercoledì in A01
        String[] dateGiulia = {"2026-04-06", "2026-04-08", "2026-04-13", "2026-04-15"};
        for (String d : dateGiulia) {
            jdbcTemplate.update("INSERT IGNORE INTO turno (data_turno, ora_inizio, ora_fine) VALUES (?, '08:00:00', '14:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rat (codice_ambulatorio, data_turno, ora_inizio, ora_fine) VALUES ('A01', ?, '08:00:00', '14:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rdt (data_turno, ora_inizio, ora_fine, cf_dipendente, presenza) VALUES (?, '08:00:00', '14:00:00', 'MED0000000000001', TRUE)", d);
        }

        // Medico 2 (Marco): Martedì e Giovedì in B01
        String[] dateMarco = {"2026-04-07", "2026-04-09", "2026-04-14", "2026-04-16"};
        for (String d : dateMarco) {
            jdbcTemplate.update("INSERT IGNORE INTO turno (data_turno, ora_inizio, ora_fine) VALUES (?, '09:00:00', '17:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rat (codice_ambulatorio, data_turno, ora_inizio, ora_fine) VALUES ('B01', ?, '09:00:00', '17:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rdt (data_turno, ora_inizio, ora_fine, cf_dipendente, presenza) VALUES (?, '09:00:00', '17:00:00', 'MED0000000000002', TRUE)", d);
        }

        // Medico 3 (Elena): Venerdì in C01 (pomeriggio)
        String[] dateElena = {"2026-04-03", "2026-04-10", "2026-04-17"};
        for (String d : dateElena) {
            jdbcTemplate.update("INSERT IGNORE INTO turno (data_turno, ora_inizio, ora_fine) VALUES (?, '14:00:00', '20:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rat (codice_ambulatorio, data_turno, ora_inizio, ora_fine) VALUES ('C01', ?, '14:00:00', '20:00:00')", d);
            jdbcTemplate.update("INSERT IGNORE INTO rdt (data_turno, ora_inizio, ora_fine, cf_dipendente, presenza) VALUES (?, '14:00:00', '20:00:00', 'MED0000000000003', TRUE)", d);
        }

        log.info("Turni RAT/RDT inizializzati con successo per medici diversi.");

        log.info("DataInitializer completato. Password di test: '{}'", TEST_PASSWORD);
    }
}

