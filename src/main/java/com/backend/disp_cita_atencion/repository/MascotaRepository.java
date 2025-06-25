package com.backend.disp_cita_atencion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.disp_cita_atencion.model.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    
}