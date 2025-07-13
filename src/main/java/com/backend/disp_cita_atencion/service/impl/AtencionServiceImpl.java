package com.backend.disp_cita_atencion.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.disp_cita_atencion.dto.request.AtencionRequestDTO;
import com.backend.disp_cita_atencion.dto.response.AtencionResponseDTO;
import com.backend.disp_cita_atencion.dto.response.InsumoResponseDTO;
import com.backend.disp_cita_atencion.dto.response.ServicioResponseDTO;
import com.backend.disp_cita_atencion.dto.response.TipoInsumoDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Atencion;
import com.backend.disp_cita_atencion.model.Cita;
import com.backend.disp_cita_atencion.model.Servicio;
import com.backend.disp_cita_atencion.repository.AtencionRepository;
import com.backend.disp_cita_atencion.repository.CitaRepository;
import com.backend.disp_cita_atencion.repository.ServicioRepository;
import com.backend.disp_cita_atencion.service.AtencionService;

@Service
public class AtencionServiceImpl implements AtencionService {

    @Autowired
    private AtencionRepository atencionRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Override
    public List<AtencionResponseDTO> obtenerAtenciones() {
        return atencionRepository.findByEstadoTrue().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public AtencionResponseDTO buscarAtencionPorId(Long id) {
        Atencion atencion = atencionRepository.findByIdAtencionAndEstadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atención no encontrada"));
        return convertirADTO(atencion);
    }

    @Override
    public AtencionResponseDTO crearAtencion(AtencionRequestDTO dto) {
        Cita cita = citaRepository.findById(dto.getCitaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

        if (!"ACTIVA".equalsIgnoreCase(cita.getEstado())) {
            throw new IllegalStateException("Solo se puede crear una atención para citas en estado ACTIVA.");
        }

        List<Servicio> servicios = servicioRepository.findAllById(dto.getServicioIds());

        // Se cambia el estado de la cita a FINALIZADA
        cita.setEstado("FINALIZADA");
        citaRepository.save(cita);

        Atencion atencion = new Atencion();
        atencion.setDiagnostico(dto.getDiagnostico());
        atencion.setTratamiento(dto.getTratamiento());
        atencion.setObservaciones(dto.getObservaciones());
        atencion.setFecha(dto.getFecha());
        atencion.setTotalCosto(dto.getTotalCosto());
        atencion.setUsernameKeycloak(dto.getUsernameKeycloak());
        atencion.setCita(cita);
        atencion.setServicios(servicios);
        atencion.setEstado(true);

        return convertirADTO(atencionRepository.save(atencion));
    }

    @Override
    public AtencionResponseDTO actualizarAtencion(Long id, AtencionRequestDTO dto) {
        Atencion atencion = atencionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atención no encontrada"));

        atencion.setDiagnostico(dto.getDiagnostico());
        atencion.setTratamiento(dto.getTratamiento());
        atencion.setObservaciones(dto.getObservaciones());
        atencion.setFecha(dto.getFecha());
        atencion.setTotalCosto(dto.getTotalCosto());
        atencion.setUsernameKeycloak(dto.getUsernameKeycloak());

        if (dto.getCitaId() != null) {
            Cita cita = citaRepository.findById(dto.getCitaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));
            atencion.setCita(cita);
        }

        if (dto.getServicioIds() != null) {
            List<Servicio> servicios = servicioRepository.findAllById(dto.getServicioIds());
            atencion.setServicios(servicios);
        }

        return convertirADTO(atencionRepository.save(atencion));
    }

    @Override
    public void eliminarAtencion(Long id) {
        Atencion atencion = atencionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Atención no encontrada"));

        atencion.setEstado(false);

        // Cita cita = atencion.getCita();
        // if (cita != null) {
        // cita.setAtencion(null);
        // citaRepository.save(cita);
        // }

        atencionRepository.save(atencion);
    }

    @Override
    public List<AtencionResponseDTO> buscarAtencionesPorUsuario(String usernameKeycloak, Boolean estado) {
        List<Atencion> atenciones;

        if (estado != null) {
            atenciones = atencionRepository.findByUsernameKeycloakAndEstado(usernameKeycloak, estado);
        } else {
            atenciones = atencionRepository.findByUsernameKeycloak(usernameKeycloak);
        }

        return atenciones.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private AtencionResponseDTO convertirADTO(Atencion atencion) {
        AtencionResponseDTO dto = new AtencionResponseDTO();
        dto.setId(atencion.getIdAtencion());
        dto.setDiagnostico(atencion.getDiagnostico());
        dto.setTratamiento(atencion.getTratamiento());
        dto.setObservaciones(atencion.getObservaciones());
        dto.setFecha(atencion.getFecha());
        dto.setTotalCosto(atencion.getTotalCosto());
        dto.setUsernameKeycloak(atencion.getUsernameKeycloak());
        dto.setCitaId(atencion.getCita() != null ? atencion.getCita().getIdCita() : null);

        dto.setServicios(atencion.getServicios().stream()
                .map(servicio -> {
                    ServicioResponseDTO servicioDTO = new ServicioResponseDTO();
                    servicioDTO.setId(servicio.getIdServicio());
                    servicioDTO.setNombreServicio(servicio.getNombreServicio());
                    servicioDTO.setPrecio(servicio.getPrecio());
                    servicioDTO.setEstado(servicio.getEstado());

                    servicioDTO.setInsumos(servicio.getInsumos().stream()
                            .map(insumo -> {
                                InsumoResponseDTO insumoDTO = new InsumoResponseDTO();
                                insumoDTO.setId(insumo.getIdInsumo());
                                insumoDTO.setNombre(insumo.getNombre());
                                insumoDTO.setCantidadUsada(insumo.getCantidadUsada());
                                insumoDTO.setPrecioUnitario(insumo.getPrecioUnitario());
                                insumoDTO.setEstado(insumo.getEstado());

                                if (insumo.getTipoInsumo() != null) {
                                    TipoInsumoDTO tipoDTO = new TipoInsumoDTO();
                                    tipoDTO.setId(insumo.getTipoInsumo().getIdTipoInsumo());
                                    tipoDTO.setNombreTipo(insumo.getTipoInsumo().getNombreTipo());
                                    tipoDTO.setEstado(insumo.getTipoInsumo().getEstado());
                                    insumoDTO.setTipoInsumo(tipoDTO);
                                } else {
                                    insumoDTO.setTipoInsumo(null);
                                }

                                return insumoDTO;
                            })
                            .collect(Collectors.toList()));

                    return servicioDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
