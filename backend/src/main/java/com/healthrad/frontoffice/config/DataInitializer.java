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
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * DataInitializer: popola il DB con utenti di test al primo avvio.
 * Usa la stessa libreria jBCrypt usata per la verifica, eliminando
 * qualsiasi problema di compatibilità degli hash.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UtenteRepository utenteRepository;
    @Autowired private DipendenteRepository dipendenteRepository;
    @Autowired private ClienteRepository clienteRepository;

    // Password in chiaro per i test — sarà hashata a runtime con jBCrypt
    private static final String TEST_PASSWORD = "password";

    @Override
    public void run(ApplicationArguments args) {

        // Genera l'hash una volta sola con la libreria jBCrypt
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
        fo.setPassword(hash);  // Aggiorna SEMPRE con hash fresco
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

        log.info("DataInitializer completato. Password di test: '{}'", TEST_PASSWORD);
    }
}
