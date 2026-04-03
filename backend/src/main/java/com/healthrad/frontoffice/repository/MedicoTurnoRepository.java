package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MedicoTurnoRepository extends JpaRepository<Dipendente, String> {

    /**
     * Trova i dipendenti (medici/tecnici) che hanno un turno assegnato nell'ambulatorio
     * specificato, alla data e orario della prenotazione.
     * 
     * Catena: ambulatorio → RAT (codice_ambulatorio, data_turno, ora_inizio, ora_fine)
     *       → RDT (data_turno, ora_inizio, ora_fine, cf_dipendente)
     *       → Dipendente (cf)
     */
    @Query(nativeQuery = true, value =
        "SELECT d.* FROM dipendente d " +
        "JOIN utente u ON d.cf = u.cf " +
        "JOIN rdt r ON r.cf_dipendente = d.cf " +
        "JOIN rat a ON a.data_turno = r.data_turno AND a.ora_inizio = r.ora_inizio AND a.ora_fine = r.ora_fine " +
        "WHERE a.codice_ambulatorio = :codAmb " +
        "AND a.data_turno = :dataPren " +
        "AND :oraPren BETWEEN r.ora_inizio AND r.ora_fine " +
        "LIMIT 1")
    List<Dipendente> findMedicoByAmbulatorioAndDataAndOra(
        @Param("codAmb") String codiceAmbulatorio,
        @Param("dataPren") LocalDate dataPrenotazione,
        @Param("oraPren") LocalTime orarioPrenotazione
    );
}
