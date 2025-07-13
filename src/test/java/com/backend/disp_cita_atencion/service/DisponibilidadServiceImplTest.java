package com.backend.disp_cita_atencion.service;

import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Disponibilidad;
import com.backend.disp_cita_atencion.repository.DisponibilidadRepository;
import com.backend.disp_cita_atencion.service.impl.DisponibilidadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisponibilidadServiceImplTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @InjectMocks
    private DisponibilidadServiceImpl disponibilidadService;

    private Disponibilidad disponibilidadActiva;
    private Disponibilidad disponibilidadInactiva;
    private Disponibilidad disponibilidadVeterinario1Fecha1;

    @BeforeEach
    void setUp() {
        disponibilidadActiva = new Disponibilidad(1L, LocalDate.of(2025, 7, 15), "09:00", "10:00", true, "veterinario1");
        disponibilidadInactiva = new Disponibilidad(2L, LocalDate.of(2025, 7, 16), "10:00", "11:00", false, "veterinario2");
        disponibilidadVeterinario1Fecha1 = new Disponibilidad(3L, LocalDate.of(2025, 7, 17), "14:00", "15:00", true, "veterinario1");
    }

    @Test
    @DisplayName("Obtener Disponibilidades Activas - Debería retornar solo las disponibilidades con estado true")
    void testObtenerDisponibilidadesActivas() {
        // Arrange
        List<Disponibilidad> allDisponibilidades = Arrays.asList(disponibilidadActiva, disponibilidadInactiva);
        when(disponibilidadRepository.findAll()).thenReturn(allDisponibilidades);

        // Act
        List<DisponibilidadResponseDTO> result = disponibilidadService.obtenerDisponibilidadesActivas();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(disponibilidadActiva.getIdDisponibilidad(), result.get(0).getId());
        assertTrue(result.get(0).getDisponible());
        verify(disponibilidadRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar Disponibilidad por ID - Debería retornar el DTO si la disponibilidad existe")
    void testBuscarPorId_Existente() {
        // Arrange
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidadActiva));

        // Act
        DisponibilidadResponseDTO result = disponibilidadService.buscarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(disponibilidadActiva.getIdDisponibilidad(), result.getId());
        assertEquals(disponibilidadActiva.getFecha(), result.getFecha());
        verify(disponibilidadRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar Disponibilidad por ID - Debería lanzar ResourceNotFoundException si la disponibilidad no existe")
    void testBuscarPorId_NoExistente() {
        // Arrange
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> disponibilidadService.buscarPorId(99L));
        verify(disponibilidadRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Crear Disponibilidad - Debería guardar una nueva disponibilidad correctamente")
    void testCrearDisponibilidad_Exito() {
        // Arrange
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 7, 19), "11:00", "12:00", true, "veterinario3");
        Disponibilidad savedDisponibilidad = new Disponibilidad(3L, LocalDate.of(2025, 7, 19), "11:00", "12:00", true, "veterinario3");

        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(savedDisponibilidad);

        // Act
        DisponibilidadResponseDTO result = disponibilidadService.crearDisponibilidad(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(savedDisponibilidad.getIdDisponibilidad(), result.getId());
        assertEquals(requestDTO.getFecha(), result.getFecha());
        assertEquals(requestDTO.getHoraInicio(), result.getHoraInicio());
        assertTrue(result.getDisponible());
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }
    
    @Test
    @DisplayName("Crear Disponibilidad - Debería establecer disponible en true si no se especifica en el DTO")
    void testCrearDisponibilidad_DisponibleDefaultTrue() {
        // Arrange
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 7, 20), "08:00", "09:00", null, "veterinario4");
        Disponibilidad savedDisponibilidad = new Disponibilidad(4L, LocalDate.of(2025, 7, 20), "08:00", "09:00", true, "veterinario4");

        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(savedDisponibilidad);

        // Act
        DisponibilidadResponseDTO result = disponibilidadService.crearDisponibilidad(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getDisponible());
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    @DisplayName("Actualizar Disponibilidad - Debería modificar una disponibilidad existente")
    void testActualizarDisponibilidad_Exito() {
        // Arrange
        Long idToUpdate = 1L;
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 7, 15), "09:30", "10:30", false, "veterinario1");

        when(disponibilidadRepository.findById(idToUpdate)).thenReturn(Optional.of(disponibilidadActiva));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(invocation -> {
            Disponibilidad d = invocation.getArgument(0);
            d.setIdDisponibilidad(idToUpdate); // Aseguramos que el ID se mantiene
            return d;
        });

        // Act
        DisponibilidadResponseDTO result = disponibilidadService.actualizarDisponibilidad(idToUpdate, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(idToUpdate, result.getId());
        assertEquals("09:30", result.getHoraInicio());
        assertEquals("10:30", result.getHoraFin());
        assertFalse(result.getDisponible());
        verify(disponibilidadRepository, times(1)).findById(idToUpdate);
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    @DisplayName("Actualizar Disponibilidad - Debería lanzar ResourceNotFoundException si la disponibilidad no existe")
    void testActualizarDisponibilidad_NoExistente() {
        // Arrange
        Long idToUpdate = 99L;
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 7, 21), "09:00", "10:00", true, "veterinarioX");
        when(disponibilidadRepository.findById(idToUpdate)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> disponibilidadService.actualizarDisponibilidad(idToUpdate, requestDTO));
        verify(disponibilidadRepository, times(1)).findById(idToUpdate);
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    @Test
    @DisplayName("Eliminar Disponibilidad - Debería cambiar el estado a false (eliminación lógica)")
    void testEliminarDisponibilidad_Exito() {
        // Arrange
        Long idToDelete = 1L;
        when(disponibilidadRepository.findById(idToDelete)).thenReturn(Optional.of(disponibilidadActiva));

        // Act
        disponibilidadService.eliminarDisponibilidad(idToDelete);

        // Assert
        assertFalse(disponibilidadActiva.getDisponible()); // Verifica que el estado se actualizó en el objeto mock
        verify(disponibilidadRepository, times(1)).findById(idToDelete);
        verify(disponibilidadRepository, times(1)).save(disponibilidadActiva); // Verifica que el objeto modificado fue guardado
    }

    @Test
    @DisplayName("Eliminar Disponibilidad - Debería lanzar ResourceNotFoundException si la disponibilidad no existe")
    void testEliminarDisponibilidad_NoExistente() {
        // Arrange
        Long idToDelete = 99L;
        when(disponibilidadRepository.findById(idToDelete)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> disponibilidadService.eliminarDisponibilidad(idToDelete));
        verify(disponibilidadRepository, times(1)).findById(idToDelete);
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    @Test
    @DisplayName("Buscar Disponibilidades por Veterinario y Fecha - Debería retornar disponibilidades activas para el veterinario y fecha especificados")
    void testBuscarDisponibilidadesPorVeterinarioYFecha() {
        // Arrange
        String username = "veterinario1";
        LocalDate fecha = LocalDate.of(2025, 7, 17);
        List<Disponibilidad> foundDisponibilidades = Arrays.asList(disponibilidadVeterinario1Fecha1);

        when(disponibilidadRepository.findByUsernameKeycloakAndFechaAndDisponibleTrue(username, fecha)).thenReturn(foundDisponibilidades);

        // Act
        List<DisponibilidadResponseDTO> result = disponibilidadService.buscarDisponibilidadesPorVeterinario(username, fecha);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(disponibilidadVeterinario1Fecha1.getIdDisponibilidad(), result.get(0).getId());
        assertEquals(username, result.get(0).getUsernameKeycloak());
        assertEquals(fecha, result.get(0).getFecha());
        assertTrue(result.get(0).getDisponible());
        verify(disponibilidadRepository, times(1)).findByUsernameKeycloakAndFechaAndDisponibleTrue(username, fecha);
    }

    @Test
    @DisplayName("Buscar Disponibilidades por Veterinario y Fecha - Debería retornar lista vacía si no hay coincidencias")
    void testBuscarDisponibilidadesPorVeterinarioYFecha_NoCoincidencias() {
        // Arrange
        String username = "veterinarioInexistente";
        LocalDate fecha = LocalDate.of(2025, 7, 22);
        when(disponibilidadRepository.findByUsernameKeycloakAndFechaAndDisponibleTrue(username, fecha)).thenReturn(Arrays.asList());

        // Act
        List<DisponibilidadResponseDTO> result = disponibilidadService.buscarDisponibilidadesPorVeterinario(username, fecha);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disponibilidadRepository, times(1)).findByUsernameKeycloakAndFechaAndDisponibleTrue(username, fecha);
    }

    @Test
    @DisplayName("Obtener Fechas Disponibles por Veterinario - Debería retornar una lista de fechas disponibles")
    void testObtenerFechasDisponiblesPorVeterinario() {
        // Arrange
        String username = "veterinario1";
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2025, 7, 15), LocalDate.of(2025, 7, 17), LocalDate.of(2025, 7, 18));
        when(disponibilidadRepository.findFechasDisponiblesByUsernameKeycloak(username)).thenReturn(expectedDates);

        // Act
        List<LocalDate> result = disponibilidadService.obtenerFechasDisponiblesPorVeterinario(username);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedDates, result);
        verify(disponibilidadRepository, times(1)).findFechasDisponiblesByUsernameKeycloak(username);
    }

    @Test
    @DisplayName("Obtener Fechas Disponibles por Veterinario - Debería retornar una lista vacía si no hay fechas disponibles")
    void testObtenerFechasDisponiblesPorVeterinario_NoFechas() {
        // Arrange
        String username = "veterinarioSinFechas";
        when(disponibilidadRepository.findFechasDisponiblesByUsernameKeycloak(username)).thenReturn(Arrays.asList());

        // Act
        List<LocalDate> result = disponibilidadService.obtenerFechasDisponiblesPorVeterinario(username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(disponibilidadRepository, times(1)).findFechasDisponiblesByUsernameKeycloak(username);
    }
}
