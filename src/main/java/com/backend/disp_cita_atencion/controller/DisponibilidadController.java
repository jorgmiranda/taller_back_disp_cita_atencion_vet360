package com.backend.disp_cita_atencion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.response.ApiResponse;
import com.backend.disp_cita_atencion.service.DisponibilidadService;

@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DisponibilidadResponseDTO>>> getAll() {
        List<DisponibilidadResponseDTO> lista = disponibilidadService.obtenerDisponibilidadesActivas();
        return ResponseEntity.ok(ApiResponse.exito("Listado exitoso", lista));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> getById(@PathVariable Long id) {
        DisponibilidadResponseDTO dto = disponibilidadService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.exito("Encontrado correctamente", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> create(@RequestBody DisponibilidadRequestDTO dto) {
        DisponibilidadResponseDTO creado = disponibilidadService.crearDisponibilidad(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.exito("Creado correctamente", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> update(@PathVariable Long id, @RequestBody DisponibilidadRequestDTO dto) {
        DisponibilidadResponseDTO actualizado = disponibilidadService.actualizarDisponibilidad(id, dto);
        return ResponseEntity.ok(ApiResponse.exito("Actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        disponibilidadService.eliminarDisponibilidad(id);
        return ResponseEntity.ok(ApiResponse.exito("Eliminado correctamente", null));
    }
}