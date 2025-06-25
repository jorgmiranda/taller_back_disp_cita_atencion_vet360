package com.backend.disp_cita_atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.disp_cita_atencion.model.Cita;

public interface CitaRepository extends JpaRepository<Cita, Long> {
}