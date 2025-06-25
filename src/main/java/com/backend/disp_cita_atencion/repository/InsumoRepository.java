package com.backend.disp_cita_atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.disp_cita_atencion.model.Insumo;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
}
