package com.backend.disp_cita_atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.disp_cita_atencion.model.Disponibilidad;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByUsernameKeycloakAndFechaAndDisponibleTrue(String usernameKeycloak, LocalDate fecha);
    
    @Query("SELECT DISTINCT d.fecha FROM Disponibilidad d WHERE d.usernameKeycloak = :username AND d.disponible = true AND d.fecha >= CURRENT_DATE ORDER BY d.fecha ASC")
    List<LocalDate> findFechasDisponiblesByUsernameKeycloak(@Param("username") String username);
}