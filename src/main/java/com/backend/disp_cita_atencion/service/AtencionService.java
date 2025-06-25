package com.backend.disp_cita_atencion.service;

import java.util.List;

import com.backend.disp_cita_atencion.dto.request.AtencionRequestDTO;
import com.backend.disp_cita_atencion.dto.response.AtencionResponseDTO;

public interface AtencionService {

    List<AtencionResponseDTO> obtenerAtenciones();

    AtencionResponseDTO buscarAtencionPorId(Long id);

    AtencionResponseDTO crearAtencion(AtencionRequestDTO dto);

    AtencionResponseDTO actualizarAtencion(Long id, AtencionRequestDTO dto);

    void eliminarAtencion(Long id);
}
