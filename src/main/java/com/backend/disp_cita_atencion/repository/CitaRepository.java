package com.backend.disp_cita_atencion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.disp_cita_atencion.model.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    @Query("SELECT c FROM Cita c WHERE c.disponibilidad.usernameKeycloak = :username")
    List<Cita> findByUsernameKeycloak(@Param("username") String username);
}