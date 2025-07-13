package com.backend.disp_cita_atencion.controller;

import com.backend.disp_cita_atencion.dto.request.CitaRequestDTO;
import com.backend.disp_cita_atencion.dto.response.CitaResponseDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.dto.response.MascotaDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.service.CitaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Para LocalDate en DisponibilidadResponseDTO
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitaController.class)
@Import(CitaControllerTest.TestSecurityConfig.class)
public class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaService citaService;

    @Autowired
    private ObjectMapper objectMapper;

    private CitaResponseDTO citaDTO1;
    private CitaResponseDTO citaDTO2;
    private DisponibilidadResponseDTO dispDTO1;
    private MascotaDTO mascotaDTO1;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDate en DisponibilidadResponseDTO
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para formatear Date como String

        dispDTO1 = new DisponibilidadResponseDTO(10L, LocalDate.of(2025, 7, 15), "09:00", "10:00", true, "veterinarioA");
        mascotaDTO1 = new MascotaDTO(1L, "CHIP123", "Max", new Date(), "Macho", true, null, null); // Asumiendo MascotaDTO

        citaDTO1 = new CitaResponseDTO(1L, new Date(), new Date(), "PENDIENTE", "Revisión general", "cliente123", dispDTO1, mascotaDTO1);
        citaDTO2 = new CitaResponseDTO(2L, new Date(), new Date(), "CANCELADA", "Vacunación", "cliente456", dispDTO1, mascotaDTO1);
    }

    // --- Pruebas para GET /api/cita (obtener todos activos) ---
    @Test
    @DisplayName("GET /api/cita - Con rol ADMIN, debería retornar una lista de citas activas")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAll_ConAdmin_RetornaListaActivas() throws Exception {
        List<CitaResponseDTO> activas = Arrays.asList(citaDTO1);
        when(citaService.obtenerCitasActivas()).thenReturn(activas);

        mockMvc.perform(get("/api/cita")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Listado exitoso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].estado").value("PENDIENTE"));

        verify(citaService, times(1)).obtenerCitasActivas();
    }

    @Test
    @DisplayName("GET /api/cita - Sin autenticación, debería retornar 401 Unauthorized")
    void testGetAll_SinAutenticacion_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/cita")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(citaService, never()).obtenerCitasActivas();
    }

    @Test
    @DisplayName("GET /api/cita - Con rol no autorizado (ej. USER), debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetAll_ConRolNoAutorizado_Forbidden() throws Exception {
        mockMvc.perform(get("/api/cita")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(citaService, never()).obtenerCitasActivas();
    }


    // --- Pruebas para GET /api/cita/{id} (buscar por ID) ---
    @Test
    @DisplayName("GET /api/cita/{id} - Con rol VETERINARIO y ID existente, debería retornar la cita")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testGetById_Existente_RetornaCita() throws Exception {
        when(citaService.buscarCitaPorId(1L)).thenReturn(citaDTO1);

        mockMvc.perform(get("/api/cita/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.motivo").value("Revisión general"));

        verify(citaService, times(1)).buscarCitaPorId(1L);
    }

    @Test
    @DisplayName("GET /api/cita/{id} - Con rol ASISTENTE y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testGetById_NoExistente_RetornaNotFound() throws Exception {
        when(citaService.buscarCitaPorId(99L)).thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        mockMvc.perform(get("/api/cita/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Cita no encontrada"));

        verify(citaService, times(1)).buscarCitaPorId(99L);
    }

    // --- Pruebas para POST /api/cita (crear cita) ---
    @Test
    @DisplayName("POST /api/cita - Con rol ADMIN, debería crear una cita y retornar 201 Created")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate_ConAdmin_RetornaCreado() throws Exception {
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Consulta inicial", "cliente999", 10L, 1L);
        CitaResponseDTO createdResponseDTO = new CitaResponseDTO(3L, new Date(), new Date(), "PROGRAMADA", "Consulta inicial", "cliente999", dispDTO1, mascotaDTO1);

        when(citaService.crearCita(any(CitaRequestDTO.class))).thenReturn(createdResponseDTO);

        mockMvc.perform(post("/api/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Creado correctamente"))
                .andExpect(jsonPath("$.data.id").value(3L))
                .andExpect(jsonPath("$.data.estado").value("PROGRAMADA"));

        verify(citaService, times(1)).crearCita(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/cita - Sin autenticación, debería retornar 401 Unauthorized")
    void testCreate_SinAutenticacion_Unauthorized() throws Exception {
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Motivo", "user", 1L, 1L);
        mockMvc.perform(post("/api/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
        verify(citaService, never()).crearCita(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/cita - Con rol no autorizado (ej. USER), debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreate_ConRolNoAutorizado_Forbidden() throws Exception {
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Motivo", "user", 1L, 1L);
        mockMvc.perform(post("/api/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
        verify(citaService, never()).crearCita(any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/cita - Debería retornar 404 Not Found si disponibilidad o mascota no existen")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testCreate_DisponibilidadOMascotaNoEncontrada_NotFound() throws Exception {
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PROGRAMADA", "Consulta", "cliente", 99L, 1L);
        when(citaService.crearCita(any(CitaRequestDTO.class))).thenThrow(new ResourceNotFoundException("Disponibilidad no encontrada"));

        mockMvc.perform(post("/api/cita")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Disponibilidad no encontrada"));

        verify(citaService, times(1)).crearCita(any(CitaRequestDTO.class));
    }

    // --- Pruebas para PUT /api/cita/{id} (actualizar cita) ---
    @Test
    @DisplayName("PUT /api/cita/{id} - Con rol ASISTENTE y ID existente, debería actualizar la cita")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testUpdate_ConAsistente_RetornaActualizado() throws Exception {
        Long idToUpdate = 1L;
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "REAGENDADA", "Chequeo reagendado", "cliente123", dispDTO1.getId(), mascotaDTO1.getId());
        CitaResponseDTO updatedResponseDTO = new CitaResponseDTO(idToUpdate, new Date(), new Date(), "REAGENDADA", "Chequeo reagendado", "cliente123", dispDTO1, mascotaDTO1);

        when(citaService.actualizarCita(eq(idToUpdate), any(CitaRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/cita/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.estado").value("REAGENDADA"))
                .andExpect(jsonPath("$.data.motivo").value("Chequeo reagendado"));

        verify(citaService, times(1)).actualizarCita(eq(idToUpdate), any(CitaRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/cita/{id} - Con rol ADMIN y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdate_CitaNoEncontrada_RetornaNotFound() throws Exception {
        Long idToUpdate = 99L;
        CitaRequestDTO requestDTO = new CitaRequestDTO(new Date(), new Date(), "PENDIENTE", "Motivo", "user", 1L, 1L);
        when(citaService.actualizarCita(eq(idToUpdate), any(CitaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        mockMvc.perform(put("/api/cita/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(citaService, times(1)).actualizarCita(eq(idToUpdate), any(CitaRequestDTO.class));
    }

    // --- Pruebas para DELETE /api/cita/{id} (eliminar cita - cambia estado a CANCELADA) ---
    @Test
    @DisplayName("DELETE /api/cita/{id} - Con rol VETERINARIO, debería eliminar (cancelar) la cita")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testDelete_ConVeterinario_Exito() throws Exception {
        Long idToDelete = 1L;
        doNothing().when(citaService).eliminarCita(idToDelete);

        mockMvc.perform(delete("/api/cita/{id}", idToDelete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Eliminado correctamente"));

        verify(citaService, times(1)).eliminarCita(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/cita/{id} - Con rol ASISTENTE y ID no encontrado, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testDelete_NoEncontrado_RetornaNotFound() throws Exception {
        Long idToDelete = 99L;
        doThrow(new ResourceNotFoundException("Cita no encontrada para cancelar"))
                .when(citaService).eliminarCita(idToDelete);

        mockMvc.perform(delete("/api/cita/{id}", idToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(citaService, times(1)).eliminarCita(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/cita/{id} - Con rol USER, debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testDelete_ConUser_Forbidden() throws Exception {
        Long idToDelete = 1L;

        mockMvc.perform(delete("/api/cita/{id}", idToDelete))
                .andExpect(status().isForbidden());
        verify(citaService, never()).eliminarCita(anyLong());
    }

    // --- Pruebas para GET /api/cita/por-usuario/{username} ---
    @Test
    @DisplayName("GET /api/cita/por-usuario/{username} - Con rol ADMIN, debería retornar citas por username")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetCitasPorUsuarioKeycloak_Exito() throws Exception {
        String username = "cliente123";
        List<CitaResponseDTO> citas = Arrays.asList(citaDTO1);
        when(citaService.obtenerCitasPorUsernameKeycloak(username)).thenReturn(citas);

        mockMvc.perform(get("/api/cita/por-usuario/{username}", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].usernameKeycloak").value(username));

        verify(citaService, times(1)).obtenerCitasPorUsernameKeycloak(username);
    }

    @Test
    @DisplayName("GET /api/cita/por-usuario/{username}?estado={estado} - Debería retornar citas filtradas por estado")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testGetCitasPorUsuarioKeycloak_ConFiltroEstado_Exito() throws Exception {
        String username = "cliente123";
        String estado = "PENDIENTE";
        // El servicio retorna todas las citas, el controlador filtra
        List<CitaResponseDTO> allCitasForUser = Arrays.asList(citaDTO1, citaDTO2);

        when(citaService.obtenerCitasPorUsernameKeycloak(username)).thenReturn(allCitasForUser);

        mockMvc.perform(get("/api/cita/por-usuario/{username}", username)
                .param("estado", estado)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].estado").value(estado));

        verify(citaService, times(1)).obtenerCitasPorUsernameKeycloak(username);
    }


    // --- Configuración de seguridad para los tests de @WebMvcTest ---
    @org.springframework.boot.test.context.TestConfiguration
    @EnableMethodSecurity
    static class TestSecurityConfig {
        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails admin = User.withUsername("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .build();
            UserDetails veterinario = User.withUsername("veterinario")
                .password("{noop}password")
                .roles("VETERINARIO")
                .build();
            UserDetails asistente = User.withUsername("asistente")
                .password("{noop}password")
                .roles("ASISTENTE")
                .build();
            UserDetails user = User.withUsername("user")
                .password("{noop}password")
                .roles("USER")
                .build();
            
            return new InMemoryUserDetailsManager(admin, veterinario, asistente, user);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                    .requestMatchers(HttpMethod.GET, "/api/cita", "/api/cita/{id}", "/api/cita/por-usuario/{username}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.POST, "/api/cita").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.PUT, "/api/cita/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.DELETE, "/api/cita/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        String errorBody = "{\"status\":\"error\",\"message\":\"Acceso denegado. No tienes los permisos necesarios.\",\"data\":null}";
                        response.getWriter().write(errorBody);
                    })
                );

            return http.build();
        }
    }
}
