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


import com.backend.disp_cita_atencion.dto.request.CitaRequestDTO;
import com.backend.disp_cita_atencion.dto.response.CitaResponseDTO;
import com.backend.disp_cita_atencion.response.ApiResponse;
import com.backend.disp_cita_atencion.service.CitaService;

@RestController
@RequestMapping("/api/cita")
//@CrossOrigin(origins = "*")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<List<CitaResponseDTO>>> getAll() {
        List<CitaResponseDTO> citas = citaService.obtenerCitasActivas();
        return ResponseEntity.ok(ApiResponse.exito("Listado exitoso", citas));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<CitaResponseDTO>> getById(@PathVariable Long id) {
        CitaResponseDTO cita = citaService.buscarCitaPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Encontrado correctamente", cita));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<CitaResponseDTO>> create(@RequestBody CitaRequestDTO dto) {
        CitaResponseDTO creado = citaService.crearCita(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("Creado correctamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<CitaResponseDTO>> update(@PathVariable Long id, @RequestBody CitaRequestDTO dto) {
        CitaResponseDTO actualizado = citaService.actualizarCita(id, dto);
        return ResponseEntity.ok(ApiResponse.exito("Actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'ASISTENTE')") // Asistente también necesita ver todos los dueños para buscar
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        citaService.eliminarCita(id);
        return ResponseEntity.ok(ApiResponse.exito("Eliminado correctamente", null));
    }
}
