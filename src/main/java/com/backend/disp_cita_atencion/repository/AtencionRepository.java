package com.backend.disp_cita_atencion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.disp_cita_atencion.model.Atencion;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    List<Atencion> findByEstadoTrue();

    Optional<Atencion> findByIdAtencionAndEstadoTrue(Long idAtencion);

    List<Atencion> findByUsernameKeycloakAndEstado(String usernameKeycloak, Boolean estado);

    List<Atencion> findByUsernameKeycloak(String usernameKeycloak);
}