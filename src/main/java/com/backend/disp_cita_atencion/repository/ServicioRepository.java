package com.backend.disp_cita_atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.disp_cita_atencion.model.Servicio;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

}
