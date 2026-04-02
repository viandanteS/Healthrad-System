package com.healthrad.frontoffice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * MOCK: Simula l'invio di un'email contenente le credenziali d'accesso.
     * In un ambiente di produzione, qui verrebbe utilizzato JavaMailSender
     * per inoltrare l'email tramite server SMTP.
     */
    public void inviaCredenziali(String email, String passwordInChiaro) {
        log.info("===================================================================");
        log.info("📧 MOCK INVIO EMAIL A: {}", email);
        log.info("Oggetto: Benvenuto in HealthRAD - le tue credenziali di accesso");
        log.info("Messaggio: Gentile cliente,\nBenvenuto nel sistema HealthRAD. Ecco le tue credenziali d'accesso temporanee:\n" +
                 "Email: " + email + "\n" +
                 "Password: " + passwordInChiaro + "\n" +
                 "Ti invitiamo a cambiare la password al primo accesso.");
        log.info("===================================================================");
    }
}
