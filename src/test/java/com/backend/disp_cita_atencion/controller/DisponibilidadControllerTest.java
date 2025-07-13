package com.backend.disp_cita_atencion.controller;

import com.backend.disp_cita_atencion.dto.request.DisponibilidadRequestDTO;
import com.backend.disp_cita_atencion.dto.response.DisponibilidadResponseDTO;
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException; // Asegúrate de tener esta excepción
import com.backend.disp_cita_atencion.service.DisponibilidadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DisponibilidadController.class)
@Import(DisponibilidadControllerTest.TestSecurityConfig.class)
public class DisponibilidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DisponibilidadService disponibilidadService;

    @Autowired
    private ObjectMapper objectMapper;

    private DisponibilidadResponseDTO disponibilidadDTO1;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para formatear fechas como String

        disponibilidadDTO1 = new DisponibilidadResponseDTO(1L, LocalDate.of(2025, 8, 1), "09:00", "10:00", true, "veterinarioA");
    }

    // --- Pruebas para GET /api/disponibilidad (obtener todos activos) ---
    @Test
    @DisplayName("GET /api/disponibilidad - Con rol ADMIN, debería retornar una lista de disponibilidades activas")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAll_ConAdmin_RetornaListaActivos() throws Exception {
        List<DisponibilidadResponseDTO> activas = Arrays.asList(disponibilidadDTO1);
        when(disponibilidadService.obtenerDisponibilidadesActivas()).thenReturn(activas);

        mockMvc.perform(get("/api/disponibilidad")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Listado exitoso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].disponible").value(true));

        verify(disponibilidadService, times(1)).obtenerDisponibilidadesActivas();
    }

    @Test
    @DisplayName("GET /api/disponibilidad - Sin autenticación, debería retornar 401 Unauthorized")
    void testGetAll_SinAutenticacion_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/disponibilidad")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(disponibilidadService, never()).obtenerDisponibilidadesActivas();
    }

    @Test
    @DisplayName("GET /api/disponibilidad - Con rol no autorizado (ej. USER), debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetAll_ConRolNoAutorizado_Forbidden() throws Exception {
        mockMvc.perform(get("/api/disponibilidad")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(disponibilidadService, never()).obtenerDisponibilidadesActivas();
    }


    // --- Pruebas para GET /api/disponibilidad/{id} (buscar por ID) ---
    @Test
    @DisplayName("GET /api/disponibilidad/{id} - Con rol VETERINARIO y ID existente, debería retornar la disponibilidad")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testGetById_Existente_RetornaDisponibilidad() throws Exception {
        when(disponibilidadService.buscarPorId(1L)).thenReturn(disponibilidadDTO1);

        mockMvc.perform(get("/api/disponibilidad/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.usernameKeycloak").value("veterinarioA"));

        verify(disponibilidadService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/disponibilidad/{id} - Con rol ASISTENTE y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testGetById_NoExistente_RetornaNotFound() throws Exception {
        when(disponibilidadService.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Disponibilidad no encontrada"));

        mockMvc.perform(get("/api/disponibilidad/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Disponibilidad no encontrada"));

        verify(disponibilidadService, times(1)).buscarPorId(99L);
    }

    // --- Pruebas para POST /api/disponibilidad (crear disponibilidad) ---
    @Test
    @DisplayName("POST /api/disponibilidad - Con rol ADMIN, debería crear una disponibilidad y retornar 201 Created")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate_ConAdmin_RetornaCreado() throws Exception {
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 8, 3), "13:00", "14:00", true, "veterinarioC");
        DisponibilidadResponseDTO createdResponseDTO = new DisponibilidadResponseDTO(3L, LocalDate.of(2025, 8, 3), "13:00", "14:00", true, "veterinarioC");

        when(disponibilidadService.crearDisponibilidad(any(DisponibilidadRequestDTO.class))).thenReturn(createdResponseDTO);

        mockMvc.perform(post("/api/disponibilidad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Creado correctamente"))
                .andExpect(jsonPath("$.data.id").value(3L))
                .andExpect(jsonPath("$.data.usernameKeycloak").value("veterinarioC"));

        verify(disponibilidadService, times(1)).crearDisponibilidad(any(DisponibilidadRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/disponibilidad - Sin autenticación, debería retornar 401 Unauthorized")
    void testCreate_SinAutenticacion_Unauthorized() throws Exception {
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 8, 4), "08:00", "09:00", true, "testUser");
        mockMvc.perform(post("/api/disponibilidad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
        verify(disponibilidadService, never()).crearDisponibilidad(any(DisponibilidadRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/disponibilidad - Con rol no autorizado (ej. USER), debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreate_ConRolNoAutorizado_Forbidden() throws Exception {
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 8, 5), "08:00", "09:00", true, "testUser");
        mockMvc.perform(post("/api/disponibilidad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
        verify(disponibilidadService, never()).crearDisponibilidad(any(DisponibilidadRequestDTO.class));
    }

    // --- Pruebas para PUT /api/disponibilidad/{id} (actualizar disponibilidad) ---
    @Test
    @DisplayName("PUT /api/disponibilidad/{id} - Con rol VETERINARIO y ID existente, debería actualizar la disponibilidad")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testUpdate_ConVeterinario_RetornaActualizado() throws Exception {
        Long idToUpdate = 1L;
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 8, 1), "09:30", "10:30", false, "veterinarioA");
        DisponibilidadResponseDTO updatedResponseDTO = new DisponibilidadResponseDTO(idToUpdate, LocalDate.of(2025, 8, 1), "09:30", "10:30", false, "veterinarioA");

        when(disponibilidadService.actualizarDisponibilidad(eq(idToUpdate), any(DisponibilidadRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/disponibilidad/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.horaInicio").value("09:30"))
                .andExpect(jsonPath("$.data.disponible").value(false));

        verify(disponibilidadService, times(1)).actualizarDisponibilidad(eq(idToUpdate), any(DisponibilidadRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/disponibilidad/{id} - Con rol ASISTENTE y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testUpdate_DisponibilidadNoEncontrada_RetornaNotFound() throws Exception {
        Long idToUpdate = 99L;
        DisponibilidadRequestDTO requestDTO = new DisponibilidadRequestDTO(LocalDate.of(2025, 8, 6), "10:00", "11:00", true, "inexistente");
        when(disponibilidadService.actualizarDisponibilidad(eq(idToUpdate), any(DisponibilidadRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Disponibilidad no encontrada"));

        mockMvc.perform(put("/api/disponibilidad/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(disponibilidadService, times(1)).actualizarDisponibilidad(eq(idToUpdate), any(DisponibilidadRequestDTO.class));
    }

    // --- Pruebas para DELETE /api/disponibilidad/{id} (eliminar disponibilidad - cambia estado a inactivo) ---
    @Test
    @DisplayName("DELETE /api/disponibilidad/{id} - Con rol ADMIN, debería eliminar (desactivar) la disponibilidad")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDelete_ConAdmin_Exito() throws Exception {
        Long idToDelete = 1L;
        doNothing().when(disponibilidadService).eliminarDisponibilidad(idToDelete);

        mockMvc.perform(delete("/api/disponibilidad/{id}", idToDelete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Eliminado correctamente"));

        verify(disponibilidadService, times(1)).eliminarDisponibilidad(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/disponibilidad/{id} - Con rol VETERINARIO y ID no encontrado, debería retornar 404 Not Found")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testDelete_NoEncontrado_RetornaNotFound() throws Exception {
        Long idToDelete = 99L;
        doThrow(new ResourceNotFoundException("Disponibilidad no encontrada para eliminar"))
                .when(disponibilidadService).eliminarDisponibilidad(idToDelete);

        mockMvc.perform(delete("/api/disponibilidad/{id}", idToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(disponibilidadService, times(1)).eliminarDisponibilidad(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/disponibilidad/{id} - Con rol USER, debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testDelete_ConUser_Forbidden() throws Exception {
        Long idToDelete = 1L;

        mockMvc.perform(delete("/api/disponibilidad/{id}", idToDelete))
                .andExpect(status().isForbidden());
        verify(disponibilidadService, never()).eliminarDisponibilidad(anyLong());
    }

    // --- Pruebas para GET /api/disponibilidad/fechas-disponibles/{usernameKeycloak} ---
    @Test
    @DisplayName("GET /api/disponibilidad/fechas-disponibles/{usernameKeycloak} - Debería retornar fechas disponibles")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testGetFechasDisponiblesPorVeterinario_Exito() throws Exception {
        String username = "veterinarioA";
        List<LocalDate> fechas = Arrays.asList(LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5));
        when(disponibilidadService.obtenerFechasDisponiblesPorVeterinario(username)).thenReturn(fechas);

        mockMvc.perform(get("/api/disponibilidad/fechas-disponibles/{usernameKeycloak}", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0]").value("2025-08-01"))
                .andExpect(jsonPath("$.data[1]").value("2025-08-05"));

        verify(disponibilidadService, times(1)).obtenerFechasDisponiblesPorVeterinario(username);
    }

    // --- Pruebas para GET /api/disponibilidad/disponibilidades?usernameKeycloak={username}&fecha={fecha} ---
    @Test
    @DisplayName("GET /api/disponibilidad/disponibilidades - Debería retornar disponibilidades por veterinario y fecha")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testGetDisponibilidadesPorVeterinarioYFecha_Exito() throws Exception {
        String username = "veterinarioA";
        LocalDate fecha = LocalDate.of(2025, 8, 1);
        List<DisponibilidadResponseDTO> disponibilidades = Arrays.asList(disponibilidadDTO1);
        when(disponibilidadService.buscarDisponibilidadesPorVeterinario(username, fecha)).thenReturn(disponibilidades);

        mockMvc.perform(get("/api/disponibilidad/disponibilidades")
                .param("usernameKeycloak", username)
                .param("fecha", fecha.toString()) // Convertir LocalDate a String para el parámetro
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L));

        verify(disponibilidadService, times(1)).buscarDisponibilidadesPorVeterinario(username, fecha);
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
                    .requestMatchers(HttpMethod.GET, "/api/disponibilidad", "/api/disponibilidad/{id}", 
                                     "/api/disponibilidad/fechas-disponibles/{usernameKeycloak}", 
                                     "/api/disponibilidad/disponibilidades").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.POST, "/api/disponibilidad").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.PUT, "/api/disponibilidad/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.DELETE, "/api/disponibilidad/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE") // Basado en tu controlador
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
