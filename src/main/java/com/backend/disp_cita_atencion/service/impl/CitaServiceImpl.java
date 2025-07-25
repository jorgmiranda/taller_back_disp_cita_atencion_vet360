package com.backend.disp_cita_atencion.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.disp_cita_atencion.dto.request.CitaRequestDTO;
import com.backend.disp_cita_atencion.dto.response.CitaResponseDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.dto.response.MascotaDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Cita;
import com.backend.disp_cita_atencion.model.Disponibilidad;
import com.backend.disp_cita_atencion.model.Mascota;
import com.backend.disp_cita_atencion.repository.CitaRepository;
import com.backend.disp_cita_atencion.repository.DisponibilidadRepository;
import com.backend.disp_cita_atencion.repository.MascotaRepository;
import com.backend.disp_cita_atencion.service.CitaService;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public List<CitaResponseDTO> obtenerCitasActivas() {
        return citaRepository.findAll().stream()
                .filter(c -> !"CANCELADA".equalsIgnoreCase(c.getEstado()))
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public CitaResponseDTO buscarCitaPorId(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
        return convertirADTO(cita);
    }

    @Override
    public CitaResponseDTO crearCita(CitaRequestDTO dto) {
        Disponibilidad disponibilidad = disponibilidadRepository.findById(dto.getDisponibilidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));

        Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));

        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad);

        Cita cita = new Cita();
        cita.setFechaHoraInicio(dto.getFechaHoraInicio());
        cita.setFechaHoraFin(dto.getFechaHoraFin());
        cita.setEstado(dto.getEstado());
        cita.setMotivo(dto.getMotivo());
        cita.setUsernameKeycloak(dto.getUsernameKeycloak());
        cita.setDisponibilidad(disponibilidad);
        cita.setMascota(mascota);

        return convertirADTO(citaRepository.save(cita));
    }

    @Override
    public CitaResponseDTO actualizarCita(Long id, CitaRequestDTO dto) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        cita.setFechaHoraInicio(dto.getFechaHoraInicio());
        cita.setFechaHoraFin(dto.getFechaHoraFin());
        cita.setEstado(dto.getEstado());
        cita.setMotivo(dto.getMotivo());
        cita.setUsernameKeycloak(dto.getUsernameKeycloak());

        // Cambiar disponibilidad si corresponde
        if (dto.getDisponibilidadId() != null &&
                !dto.getDisponibilidadId().equals(cita.getDisponibilidad().getIdDisponibilidad())) {

            // Liberar la disponibilidad actual
            Disponibilidad disponibilidadAnterior = cita.getDisponibilidad();
            if (disponibilidadAnterior != null) {
                disponibilidadAnterior.setDisponible(true);
                disponibilidadRepository.save(disponibilidadAnterior);
            }

            // Asignar nueva disponibilidad
            Disponibilidad nuevaDisponibilidad = disponibilidadRepository.findById(dto.getDisponibilidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));
            nuevaDisponibilidad.setDisponible(false);
            disponibilidadRepository.save(nuevaDisponibilidad);

            cita.setDisponibilidad(nuevaDisponibilidad);
        }

        // Actualizar mascota si es necesario
        if (dto.getMascotaId() != null &&
                (cita.getMascota() == null || !dto.getMascotaId().equals(cita.getMascota().getIdMascota()))) {
            Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada"));
            cita.setMascota(mascota);
        }

        return convertirADTO(citaRepository.save(cita));
    }

    @Override
    public void eliminarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        // Marcar cita como cancelada
        cita.setEstado("CANCELADA");

        // Reactivar la disponibilidad asociada
        Disponibilidad disponibilidad = cita.getDisponibilidad();
        if (disponibilidad != null) {
            disponibilidad.setDisponible(true);
            disponibilidadRepository.save(disponibilidad);
        }

        citaRepository.save(cita);
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasPorUsernameKeycloak(String username) {
        List<Cita> citas = citaRepository.findByUsernameKeycloak(username);
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    private CitaResponseDTO convertirADTO(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getIdCita());
        dto.setFechaHoraInicio(cita.getFechaHoraInicio());
        dto.setFechaHoraFin(cita.getFechaHoraFin());
        dto.setEstado(cita.getEstado());
        dto.setMotivo(cita.getMotivo());
        dto.setUsernameKeycloak(cita.getUsernameKeycloak());

        // Manejo seguro de disponibilidad
        Disponibilidad disponibilidad = cita.getDisponibilidad();
        if (disponibilidad != null) {
            DisponibilidadResponseDTO disponibilidadDTO = new DisponibilidadResponseDTO(
                    disponibilidad.getIdDisponibilidad(),
                    disponibilidad.getFecha(),
                    disponibilidad.getHoraInicio(),
                    disponibilidad.getHoraFin(),
                    disponibilidad.getDisponible(),
                    disponibilidad.getUsernameKeycloak());
            dto.setDisponibilidad(disponibilidadDTO);
        } else {
            dto.setDisponibilidad(null);
        }

        // Manejo seguro de mascota
        Mascota mascota = cita.getMascota();
        if (mascota != null) {
            MascotaDTO mascotaDTO = new MascotaDTO(
                    mascota.getIdMascota(),
                    mascota.getChip(),
                    mascota.getNombre(),
                    mascota.getFechaNacimiento(),
                    mascota.getGenero(),
                    mascota.getEstado(),
                    mascota.getRaza() != null ? mascota.getRaza().getIdRaza() : null,
                    mascota.getDueno() != null ? mascota.getDueno().getIdDueno() : null);
            dto.setMascota(mascotaDTO);
        } else {
            dto.setMascota(null);
        }

        return dto;
    }
}