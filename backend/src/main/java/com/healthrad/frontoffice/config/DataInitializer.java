package com.healthrad.frontoffice.config;

import com.healthrad.frontoffice.model.Cliente;
import com.healthrad.frontoffice.model.Dipendente;
import com.healthrad.frontoffice.model.Utente;
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

        // ---- 2. Medico Specialista ----
        Dipendente medico = (Dipendente) utenteRepository.findById("MED0000000000001")
            .orElse(new Dipendente());
        medico.setCf("MED0000000000001");
        medico.setNome("Giulia");
        medico.setCognome("Bianchi");
        medico.setDataNascita(LocalDate.of(1970, 11, 12));
        medico.setTelefono("+39 333 987 6543");
        medico.setEmail("medico@healthrad.it");
        medico.setPassword(hash);
        medico.setRuolo("Medico specialista");
        dipendenteRepository.save(medico);
        log.info("Sincronizzato: medico@healthrad.it");

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

        // ---- 4. Turni, RAT e RDT per il medico ----
        log.info("DataInitializer: inserimento turni e associazioni RAT/RDT...");
        String[] date = {"2026-04-01", "2026-04-04", "2026-04-10", "2026-04-15", "2026-04-20", "2026-04-25", "2026-04-30"};
        for (String d : date) {
            jdbcTemplate.update("MERGE INTO turno KEY (data_turno, ora_inizio, ora_fine) VALUES (?, '08:00:00', '18:00:00')", d);
            jdbcTemplate.update("MERGE INTO rat KEY (codice_ambulatorio, data_turno, ora_inizio, ora_fine) VALUES ('A01', ?, '08:00:00', '18:00:00')", d);
            jdbcTemplate.update("MERGE INTO rat KEY (codice_ambulatorio, data_turno, ora_inizio, ora_fine) VALUES ('A02', ?, '08:00:00', '18:00:00')", d);
            jdbcTemplate.update("MERGE INTO rdt KEY (data_turno, ora_inizio, ora_fine, cf_dipendente) VALUES (?, '08:00:00', '18:00:00', 'MED0000000000001', TRUE)", d);
        }
        log.info("Turni RAT/RDT inseriti per il medico Giulia Bianchi");

        log.info("DataInitializer completato. Password di test: '{}'", TEST_PASSWORD);
    }
}

