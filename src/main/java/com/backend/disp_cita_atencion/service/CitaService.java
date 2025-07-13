package com.backend.disp_cita_atencion.service;

import java.util.List;

import com.backend.disp_cita_atencion.dto.request.CitaRequestDTO;
import com.backend.disp_cita_atencion.dto.response.CitaResponseDTO;

public interface CitaService {
    List<CitaResponseDTO> obtenerCitasActivas();

    CitaResponseDTO buscarCitaPorId(Long id);

    CitaResponseDTO crearCita(CitaRequestDTO dto);

    CitaResponseDTO actualizarCita(Long id, CitaRequestDTO dto);

    void eliminarCita(Long id);

    List<CitaResponseDTO> obtenerCitasPorUsernameKeycloak(String username);
}
