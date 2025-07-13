package com.backend.disp_cita_atencion.service;

import com.backend.disp_cita_atencion.dto.request.CitaRequestDTO;
import com.backend.disp_cita_atencion.dto.response.CitaResponseDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Cita;
import com.backend.disp_cita_atencion.model.Disponibilidad;
import com.backend.disp_cita_atencion.model.Mascota;
import com.backend.disp_cita_atencion.repository.CitaRepository;
import com.backend.disp_cita_atencion.repository.DisponibilidadRepository;
import com.backend.disp_cita_atencion.repository.MascotaRepository;
import com.backend.disp_cita_atencion.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate; // Para Disponibilidad
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Calendar; // Para manipular Date

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita citaActiva;
    private Cita citaCancelada;
    private Disponibilidad disponibilidad1;
    private Disponibilidad disponibilidad2;
    private Mascota mascota1;

    @BeforeEach
    void setUp() {
        // Inicializar Mascota
        mascota1 = new Mascota();
        mascota1.setIdMascota(1L);
        mascota1.setNombre("Buddy");
        mascota1.setChip("CHIP123");
        mascota1.setEstado(true);
        mascota1.setFechaNacimiento(new Date()); // O un LocalDate si es necesario ajustar
        mascota1.setGenero("Macho");
        // No se necesitan Raca o Dueno para estos tests de Cita si no se usan directamente

        // Inicializar Disponibilidad
        disponibilidad1 = new Disponibilidad();
        disponibilidad1.setIdDisponibilidad(10L);
        disponibilidad1.setFecha(LocalDate.of(2025, 7, 15));
        disponibilidad1.setHoraInicio("09:00");
        disponibilidad1.setHoraFin("10:00");
        disponibilidad1.setDisponible(true);
        disponibilidad1.setUsernameKeycloak("vet123");

        disponibilidad2 = new Disponibilidad();
        disponibilidad2.setIdDisponibilidad(11L);
        disponibilidad2.setFecha(LocalDate.of(2025, 7, 16));
        disponibilidad2.setHoraInicio("11:00");
        disponibilidad2.setHoraFin("12:00");
        disponibilidad2.setDisponible(true); // Se marca como true para que pueda ser asignada
        disponibilidad2.setUsernameKeycloak("vet456");


        // Inicializar Citas
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        Date futureDate = cal.getTime();

        citaActiva = new Cita(1L, now, futureDate, "PENDIENTE", "Revisión anual", "cliente123", disponibilidad1, mascota1, null);
        citaCancelada = new Cita(2L, now, futureDate, "CANCELADA", "Vacunación", "cliente456", disponibilidad2, mascota1, null);
    }

    // --- Tests para obtenerCitasActivas() ---
    @Test
    @DisplayName("Obtener Citas Activas - Debería retornar solo citas no canceladas")
    void testObtenerCitasActivas() {
        // Arrange
        List<Cita> allCitas = Arrays.asList(citaActiva, citaCancelada);
        when(citaRepository.findAll()).thenReturn(allCitas);

        // Act
        List<CitaResponseDTO> result = citaService.obtenerCitasActivas();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(citaActiva.getIdCita(), result.get(0).getId());
        assertEquals("PENDIENTE", result.get(0).getEstado());
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener Citas Activas - Debería retornar una lista vacía si no hay citas activas")
    void testObtenerCitasActivas_ListaVacia() {
        // Arrange
        when(citaRepository.findAll()).thenReturn(Collections.singletonList(citaCancelada)); // Solo citas canceladas

        // Act
        List<CitaResponseDTO> result = citaService.obtenerCitasActivas();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(citaRepository, times(1)).findAll();
    }


    // --- Tests para buscarCitaPorId() ---
    @Test
    @DisplayName("Buscar Cita por ID - Debería retornar el DTO de la cita si existe")
    void testBuscarCitaPorId_Existente() {
        // Arrange
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaActiva));

        // Act
        CitaResponseDTO result = citaService.buscarCitaPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(citaActiva.getIdCita(), result.getId());
        assertEquals(citaActiva.getMotivo(), result.getMotivo());
        assertEquals(disponibilidad1.getIdDisponibilidad(), result.getDisponibilidad().getId());
        assertEquals(mascota1.getIdMascota(), result.getMascota().getId());
        verify(citaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Buscar Cita por ID - Debería lanzar ResourceNotFoundException si la cita no existe")
    void testBuscarCitaPorId_NoExistente() {
        // Arrange
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citaService.buscarCitaPorId(99L));
        verify(citaRepository, times(1)).findById(99L);
    }

    // --- Tests para crearCita() ---
    @Test
    @DisplayName("Crear Cita - Debería guardar una nueva cita y actualizar la disponibilidad")
    void testCrearCita_Exito() {
        // Arrange
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Chequeo", "cliente789", 10L, 1L);
        Cita savedCita = new Cita(3L, requestDTO.getFechaHoraInicio(), requestDTO.getFechaHoraFin(), requestDTO.getEstado(), requestDTO.getMotivo(), requestDTO.getUsernameKeycloak(), disponibilidad1, mascota1, null);

        when(disponibilidadRepository.findById(10L)).thenReturn(Optional.of(disponibilidad1));
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota1));
        when(citaRepository.save(any(Cita.class))).thenReturn(savedCita);
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(disponibilidad1); // Simular el guardado de la disponibilidad

        // Act
        CitaResponseDTO result = citaService.crearCita(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(savedCita.getIdCita(), result.getId());
        assertEquals("PROGRAMADA", result.getEstado());
        assertFalse(disponibilidad1.getDisponible()); // Verificar que la disponibilidad se puso en false

        verify(disponibilidadRepository, times(1)).findById(10L);
        verify(mascotaRepository, times(1)).findById(1L);
        verify(disponibilidadRepository, times(1)).save(disponibilidad1);
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    @DisplayName("Crear Cita - Debería lanzar ResourceNotFoundException si la disponibilidad no existe")
    void testCrearCita_DisponibilidadNoEncontrada() {
        // Arrange
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Chequeo", "cliente789", 99L, 1L);
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citaService.crearCita(requestDTO));
        verify(disponibilidadRepository, times(1)).findById(99L);
        verify(mascotaRepository, never()).findById(anyLong()); // No debe buscar mascota
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("Crear Cita - Debería lanzar ResourceNotFoundException si la mascota no existe")
    void testCrearCita_MascotaNoEncontrada() {
        // Arrange
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Chequeo", "cliente789", 10L, 99L);
        when(disponibilidadRepository.findById(10L)).thenReturn(Optional.of(disponibilidad1));
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citaService.crearCita(requestDTO));
        verify(disponibilidadRepository, times(1)).findById(10L);
        verify(mascotaRepository, times(1)).findById(99L);
        verify(citaRepository, never()).save(any(Cita.class));
    }

    // --- Tests para actualizarCita() ---
    @Test
    @DisplayName("Actualizar Cita - Debería actualizar la cita sin cambiar disponibilidad/mascota si no se especifica")
    void testActualizarCita_SoloCamposBasicos() {
        // Arrange
        Long citaId = 1L;
        CitaRequestDTO requestDTO = new CitaRequestDTO(citaActiva.getFechaHoraInicio(), citaActiva.getFechaHoraFin(), "COMPLETADA", "Revisión completa", "cliente123", disponibilidad1.getIdDisponibilidad(), mascota1.getIdMascota());

        when(citaRepository.findById(citaId)).thenReturn(Optional.of(citaActiva));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaActiva);

        // Act
        CitaResponseDTO result = citaService.actualizarCita(citaId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(citaId, result.getId());
        assertEquals("COMPLETADA", result.getEstado());
        assertEquals("Revisión completa", result.getMotivo());
        // Asegúrate de que las relaciones no se hayan cambiado si no se actualizan en el DTO
        assertEquals(disponibilidad1.getIdDisponibilidad(), result.getDisponibilidad().getId());
        assertEquals(mascota1.getIdMascota(), result.getMascota().getId());

        verify(citaRepository, times(1)).findById(citaId);
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(disponibilidadRepository, never()).findById(anyLong()); // No se debe buscar una nueva disponibilidad
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class)); // Ni guardar disponibilidad
        verify(mascotaRepository, never()).findById(anyLong()); // No se debe buscar una nueva mascota
    }

    @Test
    @DisplayName("Actualizar Cita - Debería cambiar la disponibilidad y liberar la anterior")
    void testActualizarCita_CambiarDisponibilidad() {
        // Arrange
        Long citaId = 1L;
        CitaRequestDTO requestDTO = new CitaRequestDTO(citaActiva.getFechaHoraInicio(), citaActiva.getFechaHoraFin(), "PROGRAMADA", "Cambio de hora", "cliente123", disponibilidad2.getIdDisponibilidad(), mascota1.getIdMascota());

        when(citaRepository.findById(citaId)).thenReturn(Optional.of(citaActiva)); // citaActiva tiene disponibilidad1
        when(disponibilidadRepository.findById(disponibilidad2.getIdDisponibilidad())).thenReturn(Optional.of(disponibilidad2));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Mockear el save

        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
            Cita c = invocation.getArgument(0);
            c.setIdCita(citaId); // Asegurar que el ID se mantiene
            return c;
        });

        // Estado inicial de disponibilidad1 (ocupada por citaActiva)
        disponibilidad1.setDisponible(false);
        citaActiva.setDisponibilidad(disponibilidad1);


        // Act
        CitaResponseDTO result = citaService.actualizarCita(citaId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(citaId, result.getId());
        assertEquals(disponibilidad2.getIdDisponibilidad(), result.getDisponibilidad().getId());
        assertTrue(disponibilidad1.getDisponible()); // La disponibilidad anterior debe estar libre
        assertFalse(disponibilidad2.getDisponible()); // La nueva disponibilidad debe estar ocupada

        verify(citaRepository, times(1)).findById(citaId);
        verify(disponibilidadRepository, times(1)).findById(disponibilidad2.getIdDisponibilidad());
        verify(disponibilidadRepository, times(2)).save(any(Disponibilidad.class)); // Uno para liberar anterior, otro para ocupar nueva
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    @DisplayName("Actualizar Cita - Debería cambiar la mascota")
    void testActualizarCita_CambiarMascota() {
        // Arrange
        Long citaId = 1L;
        Mascota nuevaMascota = new Mascota(2L, "CHIP456", "Max", new Date(), "Macho", true, null, null);
        CitaRequestDTO requestDTO = new CitaRequestDTO(citaActiva.getFechaHoraInicio(), citaActiva.getFechaHoraFin(), "PROGRAMADA", "Cambio de mascota", "cliente123", disponibilidad1.getIdDisponibilidad(), nuevaMascota.getIdMascota());

        when(citaRepository.findById(citaId)).thenReturn(Optional.of(citaActiva));
        when(mascotaRepository.findById(nuevaMascota.getIdMascota())).thenReturn(Optional.of(nuevaMascota));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaActiva);

        // Act
        CitaResponseDTO result = citaService.actualizarCita(citaId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(citaId, result.getId());
        assertEquals(nuevaMascota.getIdMascota(), result.getMascota().getId());

        verify(citaRepository, times(1)).findById(citaId);
        verify(mascotaRepository, times(1)).findById(nuevaMascota.getIdMascota());
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class)); // No debería afectar disponibilidad
    }


    @Test
    @DisplayName("Actualizar Cita - Debería lanzar ResourceNotFoundException si la cita no existe")
    void testActualizarCita_CitaNoEncontrada() {
        // Arrange
        Long citaId = 99L;
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Motivo", "user", 1L, 1L);
        when(citaRepository.findById(citaId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citaService.actualizarCita(citaId, requestDTO));
        verify(citaRepository, times(1)).findById(citaId);
        verify(citaRepository, never()).save(any(Cita.class));
    }

    // --- Tests para eliminarCita() (eliminación lógica) ---
    @Test
    @DisplayName("Eliminar Cita - Debería cambiar el estado a CANCELADA y liberar la disponibilidad")
    void testEliminarCita_Exito() {
        // Arrange
        Long idToDelete = 1L;
        // Aseguramos que la disponibilidad de citaActiva esté en false antes de la eliminación
        disponibilidad1.setDisponible(false);
        citaActiva.setDisponibilidad(disponibilidad1);

        when(citaRepository.findById(idToDelete)).thenReturn(Optional.of(citaActiva));
        when(citaRepository.save(any(Cita.class))).thenReturn(citaActiva); // Simular el guardado de la cita modificada
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(disponibilidad1); // Simular el guardado de la disponibilidad modificada

        // Act
        citaService.eliminarCita(idToDelete);

        // Assert
        assertEquals("CANCELADA", citaActiva.getEstado()); // Verifica que el estado de la cita se actualizó
        assertTrue(disponibilidad1.getDisponible()); // Verifica que la disponibilidad se marcó como true

        verify(citaRepository, times(1)).findById(idToDelete);
        verify(citaRepository, times(1)).save(citaActiva);
        verify(disponibilidadRepository, times(1)).save(disponibilidad1);
    }

    @Test
    @DisplayName("Eliminar Cita - Debería lanzar ResourceNotFoundException si la cita no existe")
    void testEliminarCita_NoExistente() {
        // Arrange
        Long idToDelete = 99L;
        when(citaRepository.findById(idToDelete)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> citaService.eliminarCita(idToDelete));
        verify(citaRepository, times(1)).findById(idToDelete);
        verify(citaRepository, never()).save(any(Cita.class));
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    // --- Tests para obtenerCitasPorUsernameKeycloak() ---
    @Test
    @DisplayName("Obtener Citas por Username Keycloak - Debería retornar las citas asociadas al usuario")
    void testObtenerCitasPorUsernameKeycloak_Exito() {
        // Arrange
        String username = "cliente123";
        List<Cita> citasDelUsuario = Collections.singletonList(citaActiva);
        when(citaRepository.findByUsernameKeycloak(username)).thenReturn(citasDelUsuario);

        // Act
        List<CitaResponseDTO> result = citaService.obtenerCitasPorUsernameKeycloak(username);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(citaActiva.getIdCita(), result.get(0).getId());
        assertEquals(username, result.get(0).getUsernameKeycloak());
        verify(citaRepository, times(1)).findByUsernameKeycloak(username);
    }

    @Test
    @DisplayName("Obtener Citas por Username Keycloak - Debería retornar una lista vacía si no hay citas para el usuario")
    void testObtenerCitasPorUsernameKeycloak_NoCitas() {
        // Arrange
        String username = "usuarioSinCitas";
        when(citaRepository.findByUsernameKeycloak(username)).thenReturn(Collections.emptyList());

        // Act
        List<CitaResponseDTO> result = citaService.obtenerCitasPorUsernameKeycloak(username);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(citaRepository, times(1)).findByUsernameKeycloak(username);
    }
}
