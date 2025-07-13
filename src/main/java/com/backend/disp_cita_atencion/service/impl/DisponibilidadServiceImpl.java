package com.backend.disp_cita_atencion.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Disponibilidad;
import com.backend.disp_cita_atencion.repository.DisponibilidadRepository;
import com.backend.disp_cita_atencion.service.DisponibilidadService;

@Service
public class DisponibilidadServiceImpl implements DisponibilidadService {
    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Override
    public List<DisponibilidadResponseDTO> obtenerDisponibilidadesActivas() {
        return disponibilidadRepository.findAll().stream()
                .filter(Disponibilidad::getDisponible)
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public DisponibilidadResponseDTO buscarPorId(Long id) {
        Disponibilidad d = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));
        return convertirADTO(d);
    }

    @Override
    public DisponibilidadResponseDTO crearDisponibilidad(DisponibilidadRequestDTO dto) {
        Disponibilidad d = new Disponibilidad();
        d.setFecha(dto.getFecha());
        d.setHoraInicio(dto.getHoraInicio());
        d.setHoraFin(dto.getHoraFin());
        d.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        d.setUsernameKeycloak(dto.getUsernameKeycloak());

        return convertirADTO(disponibilidadRepository.save(d));
    }

    @Override
    public DisponibilidadResponseDTO actualizarDisponibilidad(Long id, DisponibilidadRequestDTO dto) {
        Disponibilidad d = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));

        d.setFecha(dto.getFecha());
        d.setHoraInicio(dto.getHoraInicio());
        d.setHoraFin(dto.getHoraFin());
        d.setDisponible(dto.getDisponible());
        d.setUsernameKeycloak(dto.getUsernameKeycloak());

        return convertirADTO(disponibilidadRepository.save(d));
    }

    @Override
    public void eliminarDisponibilidad(Long id) {
        Disponibilidad d = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));
        d.setDisponible(false);
        disponibilidadRepository.save(d);
    }

    @Override
    public List<DisponibilidadResponseDTO> buscarDisponibilidadesPorVeterinario(String usernameKeycloak,
            LocalDate fecha) {
        return disponibilidadRepository.findByUsernameKeycloakAndFechaAndDisponibleTrue(usernameKeycloak, fecha)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalDate> obtenerFechasDisponiblesPorVeterinario(String usernameKeycloak) {
        return disponibilidadRepository.findFechasDisponiblesByUsernameKeycloak(usernameKeycloak);
    }

    private DisponibilidadResponseDTO convertirADTO(Disponibilidad d) {
        DisponibilidadResponseDTO dto = new DisponibilidadResponseDTO();
        dto.setId(d.getIdDisponibilidad());
        dto.setFecha(d.getFecha());
        dto.setHoraInicio(d.getHoraInicio());
        dto.setHoraFin(d.getHoraFin());
        dto.setDisponible(d.getDisponible());
        dto.setUsernameKeycloak(d.getUsernameKeycloak());
        return dto;
    }
}
