package com.healthrad.frontoffice.repository;

import com.healthrad.frontoffice.model.Dipendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DipendenteRepository extends JpaRepository<Dipendente, String> {

    @Query(nativeQuery = true, value =
        "SELECT DISTINCT a.codice_ambulatorio FROM rat a " +
        "JOIN rdt r ON a.data_turno = r.data_turno AND a.ora_inizio = r.ora_inizio AND a.ora_fine = r.ora_fine " +
        "WHERE a.data_turno = :dataPren")
    List<String> findAmbulatoriByData(@Param("dataPren") LocalDate dataPrenotazione);

    @Query(nativeQuery = true, value =
        "SELECT r.ora_inizio, r.ora_fine FROM rdt r " +
        "JOIN rat a ON a.data_turno = r.data_turno AND a.ora_inizio = r.ora_inizio AND a.ora_fine = r.ora_fine " +
        "WHERE a.codice_ambulatorio = :codAmb " +
        "AND a.data_turno = :dataPren")
    List<Object[]> findTurniByAmbulatorioAndData(
        @Param("codAmb") String codiceAmbulatorio,
        @Param("dataPren") LocalDate dataPrenotazione
    );

    @Query(nativeQuery = true, value =
        "SELECT r.cf_dipendente FROM rdt r " +
        "JOIN rat a ON a.data_turno = r.data_turno AND a.ora_inizio = r.ora_inizio AND a.ora_fine = r.ora_fine " +
        "WHERE a.codice_ambulatorio = :codAmb " +
        "AND a.data_turno = :dataPren " +
        "AND :oraPren BETWEEN r.ora_inizio AND r.ora_fine " +
        "LIMIT 1")
    Optional<String> findCfMedicoByAmbulatorioAndDataAndOra(
        @Param("codAmb") String codiceAmbulatorio,
        @Param("dataPren") LocalDate dataPrenotazione,
        @Param("oraPren") LocalTime orarioPrenotazione
    );
}
