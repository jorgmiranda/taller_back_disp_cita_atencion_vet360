package com.backend.disp_cita_atencion.service;

import com.backend.disp_cita_atencion.dto.request.AtencionRequestDTO;
import com.backend.disp_cita_atencion.dto.response.AtencionResponseDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.model.Atencion;
import com.backend.disp_cita_atencion.model.Cita;
import com.backend.disp_cita_atencion.model.Insumo;
import com.backend.disp_cita_atencion.model.Servicio;
import com.backend.disp_cita_atencion.model.TipoInsumo;
import com.backend.disp_cita_atencion.repository.AtencionRepository;
import com.backend.disp_cita_atencion.repository.CitaRepository;
import com.backend.disp_cita_atencion.repository.ServicioRepository;
import com.backend.disp_cita_atencion.service.impl.AtencionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtencionServiceImplTest {

    @Mock
    private AtencionRepository atencionRepository;

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private AtencionServiceImpl atencionService;

    private Atencion atencionActiva;
    private Atencion atencionInactiva;
    private Cita citaActiva;
    private Cita citaFinalizada;
    private Servicio servicio1;
    private Servicio servicio2;

    @BeforeEach
    void setUp() {
        // Mock de Cita
        citaActiva = new Cita();
        citaActiva.setIdCita(1L);
        citaActiva.setEstado("ACTIVA");
        citaActiva.setFechaHoraInicio(new Date()); // No es estrictamente necesario para estos tests, pero útil
        citaActiva.setUsernameKeycloak("cliente123");

        citaFinalizada = new Cita();
        citaFinalizada.setIdCita(2L);
        citaFinalizada.setEstado("FINALIZADA");
        citaFinalizada.setFechaHoraInicio(new Date());
        citaFinalizada.setUsernameKeycloak("cliente456");

        // Mock de Servicio y sus dependencias (para convertirADTO)
        TipoInsumo tipoInsumo = new TipoInsumo(100L, "Medicamento", true);
        // CORRECCIÓN AQUÍ: Se añade el campo fechaVencimiento al constructor de Insumo
        Insumo insumo1 = new Insumo(200L, "Paracetamol", 10, new Date(), 500, true, tipoInsumo);

        servicio1 = new Servicio();
        servicio1.setIdServicio(1L);
        servicio1.setNombreServicio("Consulta Veterinaria");
        servicio1.setPrecio(5000);
        servicio1.setEstado(true);
        servicio1.setInsumos(Collections.singletonList(insumo1));

        servicio2 = new Servicio();
        servicio2.setIdServicio(2L);
        servicio2.setNombreServicio("Vacunación");
        servicio2.setPrecio(10000);
        servicio2.setEstado(true);
        servicio2.setInsumos(Collections.emptyList()); // Sin insumos para simplificar

        // Mock de Atención
        atencionActiva = new Atencion(1L, "Gripe leve", "Reposo y medicación", "Sin complicaciones", new Date(), 15000, "veterinarioA", true, citaActiva, Arrays.asList(servicio1, servicio2));
        atencionInactiva = new Atencion(2L, "Fractura", "Cirugía", "Requiere seguimiento", new Date(), 50000, "veterinarioB", false, citaFinalizada, Arrays.asList(servicio1));
    }

    // --- Tests para obtenerAtenciones() ---
    @Test
    @DisplayName("Obtener Atenciones - Debería retornar solo atenciones con estado true")
    void testObtenerAtenciones_SoloActivas() {
        // Arrange
        when(atencionRepository.findByEstadoTrue()).thenReturn(Collections.singletonList(atencionActiva));

        // Act
        List<AtencionResponseDTO> result = atencionService.obtenerAtenciones();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(atencionActiva.getIdAtencion(), result.get(0).getId());
        verify(atencionRepository, times(1)).findByEstadoTrue();
    }

    @Test
    @DisplayName("Obtener Atenciones - Debería retornar una lista vacía si no hay atenciones activas")
    void testObtenerAtenciones_ListaVacia() {
        // Arrange
        when(atencionRepository.findByEstadoTrue()).thenReturn(Collections.emptyList());

        // Act
        List<AtencionResponseDTO> result = atencionService.obtenerAtenciones();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(atencionRepository, times(1)).findByEstadoTrue();
    }

    // --- Tests para buscarAtencionPorId() ---
    @Test
    @DisplayName("Buscar Atención por ID - Debería retornar la atención si existe y está activa")
    void testBuscarAtencionPorId_ExistenteYActiva() {
        // Arrange
        when(atencionRepository.findByIdAtencionAndEstadoTrue(1L)).thenReturn(Optional.of(atencionActiva));

        // Act
        AtencionResponseDTO result = atencionService.buscarAtencionPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(atencionActiva.getIdAtencion(), result.getId());
        assertEquals(atencionActiva.getDiagnostico(), result.getDiagnostico());
        assertEquals(citaActiva.getIdCita(), result.getCitaId());
        assertEquals(atencionActiva.getServicios().size(), result.getServicios().size());
        verify(atencionRepository, times(1)).findByIdAtencionAndEstadoTrue(1L);
    }

    @Test
    @DisplayName("Buscar Atención por ID - Debería lanzar ResourceNotFoundException si la atención no existe")
    void testBuscarAtencionPorId_NoExistente() {
        // Arrange
        when(atencionRepository.findByIdAtencionAndEstadoTrue(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.buscarAtencionPorId(99L));
        verify(atencionRepository, times(1)).findByIdAtencionAndEstadoTrue(99L);
    }

    @Test
    @DisplayName("Buscar Atención por ID - Debería lanzar ResourceNotFoundException si la atención está inactiva")
    void testBuscarAtencionPorId_Inactiva() {
        // Arrange
        when(atencionRepository.findByIdAtencionAndEstadoTrue(2L)).thenReturn(Optional.empty()); // No encuentra si no está activa

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.buscarAtencionPorId(2L));
        verify(atencionRepository, times(1)).findByIdAtencionAndEstadoTrue(2L);
    }

    // --- Tests para crearAtencion() ---
    @Test
    @DisplayName("Crear Atención - Debería guardar una nueva atención y cambiar el estado de la cita a FINALIZADA")
    void testCrearAtencion_Exito() {
        // Arrange
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Chequeo", "Dieta especial", "Ninguna", new Date(), 8000, "veterinarioC", 1L, Arrays.asList(1L, 2L));
        Atencion savedAtencion = new Atencion(3L, requestDTO.getDiagnostico(), requestDTO.getTratamiento(), requestDTO.getObservaciones(), requestDTO.getFecha(), requestDTO.getTotalCosto(), requestDTO.getUsernameKeycloak(), true, citaActiva, Arrays.asList(servicio1, servicio2));

        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaActiva));
        when(servicioRepository.findAllById(anyList())).thenReturn(Arrays.asList(servicio1, servicio2));
        when(atencionRepository.save(any(Atencion.class))).thenReturn(savedAtencion);
        when(citaRepository.save(any(Cita.class))).thenReturn(citaActiva); // Para el cambio de estado de la cita

        // Act
        AtencionResponseDTO result = atencionService.crearAtencion(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(savedAtencion.getIdAtencion(), result.getId());
        assertEquals("FINALIZADA", citaActiva.getEstado()); // Verifica que el estado de la cita cambió
        assertTrue(result.getServicios().stream().anyMatch(s -> s.getId().equals(1L)));
        assertTrue(result.getServicios().stream().anyMatch(s -> s.getId().equals(2L)));

        verify(citaRepository, times(1)).findById(1L);
        verify(citaRepository, times(1)).save(citaActiva);
        verify(servicioRepository, times(1)).findAllById(anyList());
        verify(atencionRepository, times(1)).save(any(Atencion.class));
    }

    @Test
    @DisplayName("Crear Atención - Debería lanzar ResourceNotFoundException si la cita no existe")
    void testCrearAtencion_CitaNoEncontrada() {
        // Arrange
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 99L, Collections.emptyList());
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.crearAtencion(requestDTO));
        verify(citaRepository, times(1)).findById(99L);
        verify(servicioRepository, never()).findAllById(anyList());
        verify(atencionRepository, never()).save(any(Atencion.class));
    }

    @Test
    @DisplayName("Crear Atención - Debería lanzar IllegalStateException si la cita no está en estado ACTIVA")
    void testCrearAtencion_CitaNoActiva() {
        // Arrange
        Cita citaPendiente = new Cita();
        citaPendiente.setIdCita(5L);
        citaPendiente.setEstado("PENDIENTE"); // Estado no ACTIVA
        citaPendiente.setUsernameKeycloak("cliente555");

        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 5L, Collections.emptyList());
        when(citaRepository.findById(5L)).thenReturn(Optional.of(citaPendiente));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> atencionService.crearAtencion(requestDTO));
        assertEquals("Solo se puede crear una atención para citas en estado ACTIVA.", exception.getMessage());
        verify(citaRepository, times(1)).findById(5L);
        verify(citaRepository, never()).save(any(Cita.class)); // No debe guardar la cita ni la atención
        verify(atencionRepository, never()).save(any(Atencion.class));
    }

    // --- Tests para actualizarAtencion() ---
    @Test
    @DisplayName("Actualizar Atención - Debería actualizar todos los campos incluyendo cita y servicios")
    void testActualizarAtencion_Exito() {
        // Arrange
        Long atencionId = 1L;
        Cita nuevaCita = new Cita();
        nuevaCita.setIdCita(5L);
        nuevaCita.setEstado("ACTIVA");
        nuevaCita.setUsernameKeycloak("clienteNuevo");

        Servicio servicio3 = new Servicio();
        servicio3.setIdServicio(3L);
        servicio3.setNombreServicio("Radiografía");
        servicio3.setPrecio(20000);
        servicio3.setEstado(true);
        servicio3.setInsumos(new ArrayList<>()); // Para evitar que getInsumos() sea null

        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Resfriado", "Descanso", "Sin fiebre", new Date(), 10000, "veterinarioD", 5L, Arrays.asList(3L));
        
        when(atencionRepository.findById(atencionId)).thenReturn(Optional.of(atencionActiva));
        when(citaRepository.findById(5L)).thenReturn(Optional.of(nuevaCita));
        when(servicioRepository.findAllById(anyList())).thenReturn(Collections.singletonList(servicio3));
        when(atencionRepository.save(any(Atencion.class))).thenReturn(atencionActiva); // Simular el guardado

        // Act
        AtencionResponseDTO result = atencionService.actualizarAtencion(atencionId, requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(atencionId, result.getId());
        assertEquals("Resfriado", result.getDiagnostico());
        assertEquals(5L, result.getCitaId());
        assertEquals(1, result.getServicios().size());
        assertEquals(3L, result.getServicios().get(0).getId());

        verify(atencionRepository, times(1)).findById(atencionId);
        verify(citaRepository, times(1)).findById(5L);
        verify(servicioRepository, times(1)).findAllById(anyList());
        verify(atencionRepository, times(1)).save(any(Atencion.class));
    }

    @Test
    @DisplayName("Actualizar Atención - Debería lanzar ResourceNotFoundException si la atención no existe")
    void testActualizarAtencion_NoExistente() {
        // Arrange
        Long atencionId = 99L;
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 1L, Collections.emptyList());
        when(atencionRepository.findById(atencionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.actualizarAtencion(atencionId, requestDTO));
        verify(atencionRepository, times(1)).findById(atencionId);
        verify(citaRepository, never()).findById(anyLong());
        verify(servicioRepository, never()).findAllById(anyList());
        verify(atencionRepository, never()).save(any(Atencion.class));
    }

    @Test
    @DisplayName("Actualizar Atención - Debería lanzar ResourceNotFoundException si la nueva cita no existe")
    void testActualizarAtencion_NuevaCitaNoEncontrada() {
        // Arrange
        Long atencionId = 1L;
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 99L, Collections.emptyList());
        when(atencionRepository.findById(atencionId)).thenReturn(Optional.of(atencionActiva));
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.actualizarAtencion(atencionId, requestDTO));
        verify(atencionRepository, times(1)).findById(atencionId);
        verify(citaRepository, times(1)).findById(99L);
        verify(servicioRepository, never()).findAllById(anyList());
        verify(atencionRepository, never()).save(any(Atencion.class));
    }

    // --- Tests para eliminarAtencion() (eliminación lógica) ---
    @Test
    @DisplayName("Eliminar Atención - Debería cambiar el estado a false (inactiva)")
    void testEliminarAtencion_Exito() {
        // Arrange
        Long idToDelete = 1L;
        when(atencionRepository.findById(idToDelete)).thenReturn(Optional.of(atencionActiva));
        when(atencionRepository.save(any(Atencion.class))).thenReturn(atencionActiva); // Simular el guardado de la atención modificada

        // Act
        atencionService.eliminarAtencion(idToDelete);

        // Assert
        assertFalse(atencionActiva.getEstado()); // Verifica que el estado de la atención se actualizó a false
        verify(atencionRepository, times(1)).findById(idToDelete);
        verify(atencionRepository, times(1)).save(atencionActiva);
    }

    @Test
    @DisplayName("Eliminar Atención - Debería lanzar ResourceNotFoundException si la atención no existe")
    void testEliminarAtencion_NoExistente() {
        // Arrange
        Long idToDelete = 99L;
        when(atencionRepository.findById(idToDelete)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> atencionService.eliminarAtencion(idToDelete));
        verify(atencionRepository, times(1)).findById(idToDelete);
        verify(atencionRepository, never()).save(any(Atencion.class));
    }

    // --- Tests para buscarAtencionesPorUsuario() ---
    @Test
    @DisplayName("Buscar Atenciones por Usuario - Debería retornar todas las atenciones para un usuario si estado es null")
    void testBuscarAtencionesPorUsuario_EstadoNull() {
        // Arrange
        String username = "veterinarioA";
        List<Atencion> atencionesDelUsuario = Arrays.asList(atencionActiva, atencionInactiva);
        when(atencionRepository.findByUsernameKeycloak(username)).thenReturn(atencionesDelUsuario);

        // Act
        List<AtencionResponseDTO> result = atencionService.buscarAtencionesPorUsuario(username, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getId().equals(1L)));
        assertTrue(result.stream().anyMatch(a -> a.getId().equals(2L)));
        verify(atencionRepository, times(1)).findByUsernameKeycloak(username);
        verify(atencionRepository, never()).findByUsernameKeycloakAndEstado(anyString(), anyBoolean());
    }

    @Test
    @DisplayName("Buscar Atenciones por Usuario - Debería retornar atenciones por usuario y estado true")
    void testBuscarAtencionesPorUsuario_EstadoTrue() {
        // Arrange
        String username = "veterinarioA";
        List<Atencion> atencionesDelUsuarioActivas = Collections.singletonList(atencionActiva);
        when(atencionRepository.findByUsernameKeycloakAndEstado(username, true)).thenReturn(atencionesDelUsuarioActivas);

        // Act
        List<AtencionResponseDTO> result = atencionService.buscarAtencionesPorUsuario(username, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(atencionActiva.getIdAtencion(), result.get(0).getId());
        verify(atencionRepository, times(1)).findByUsernameKeycloakAndEstado(username, true);
        verify(atencionRepository, never()).findByUsernameKeycloak(anyString());
    }

    @Test
    @DisplayName("Buscar Atenciones por Usuario - Debería retornar una lista vacía si no hay atenciones para el usuario")
    void testBuscarAtencionesPorUsuario_ListaVacia() {
        // Arrange
        String username = "usuarioSinAtenciones";
        when(atencionRepository.findByUsernameKeycloak(username)).thenReturn(Collections.emptyList());

        // Act
        List<AtencionResponseDTO> result = atencionService.buscarAtencionesPorUsuario(username, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(atencionRepository, times(1)).findByUsernameKeycloak(username);
    }

}
