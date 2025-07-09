package com.backend.disp_cita_atencion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.disp_cita_atencion.dto.request.AtencionRequestDTO;
import com.backend.disp_cita_atencion.dto.response.AtencionResponseDTO;
import com.backend.disp_cita_atencion.response.ApiResponse;
import com.backend.disp_cita_atencion.service.AtencionService;

@RestController
@RequestMapping("/api/atencion")
//@CrossOrigin(origins = "*")
public class AtencionController {

    @Autowired
    private AtencionService atencionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<List<AtencionResponseDTO>>> getAll() {
        List<AtencionResponseDTO> atenciones = atencionService.obtenerAtenciones();
        return ResponseEntity.ok(ApiResponse.exito("Listado exitoso", atenciones));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<AtencionResponseDTO>> getById(@PathVariable Long id) {
        AtencionResponseDTO atencion = atencionService.buscarAtencionPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Encontrado correctamente", atencion));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<AtencionResponseDTO>> create(@RequestBody AtencionRequestDTO dto) {
        AtencionResponseDTO creado = atencionService.crearAtencion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("Creado correctamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<AtencionResponseDTO>> update(@PathVariable Long id, @RequestBody AtencionRequestDTO dto) {
        AtencionResponseDTO actualizado = atencionService.actualizarAtencion(id, dto);
        return ResponseEntity.ok(ApiResponse.exito("Actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        atencionService.eliminarAtencion(id);
        return ResponseEntity.ok(ApiResponse.exito("Eliminado correctamente", null));
    }
}
