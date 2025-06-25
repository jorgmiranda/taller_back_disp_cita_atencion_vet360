package com.backend.disp_cita_atencion.service;

import java.util.List;

import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;

public interface DisponibilidadService {
    List<DisponibilidadResponseDTO> obtenerDisponibilidadesActivas();

    DisponibilidadResponseDTO buscarPorId(Long id);

    DisponibilidadResponseDTO crearDisponibilidad(DisponibilidadRequestDTO dto);

    DisponibilidadResponseDTO actualizarDisponibilidad(Long id, DisponibilidadRequestDTO dto);

    void eliminarDisponibilidad(Long id);
}