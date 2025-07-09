package com.backend.disp_cita_atencion.service;

import java.time.LocalDate;
import java.util.List;

import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;

public interface DisponibilidadService {
    List<DisponibilidadResponseDTO> obtenerDisponibilidadesActivas();

    DisponibilidadResponseDTO buscarPorId(Long id);

    List<DisponibilidadResponseDTO> buscarDisponibilidadesPorVeterinario(String usernameKeycloak,LocalDate fecha);

    DisponibilidadResponseDTO crearDisponibilidad(DisponibilidadRequestDTO dto);

    DisponibilidadResponseDTO actualizarDisponibilidad(Long id, DisponibilidadRequestDTO dto);

    void eliminarDisponibilidad(Long id);

    List<LocalDate> obtenerFechasDisponiblesPorVeterinario(String usernameKeycloak);
}