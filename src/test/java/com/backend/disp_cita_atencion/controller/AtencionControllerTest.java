package com.backend.disp_cita_atencion.controller;

import com.backend.disp_cita_atencion.dto.request.AtencionRequestDTO;
import com.backend.disp_cita_atencion.dto.response.AtencionResponseDTO;
import com.backend.disp_cita_atencion.dto.response.InsumoResponseDTO; // Import para DTOs anidados
import com.backend.disp_cita_atencion.dto.response.ServicioResponseDTO; // Import para DTOs anidados
import com.backend.disp_cita_atencion.dto.response.TipoInsumoDTO; // Import para DTOs anidados
import com.backend.disp_cita_atencion.exception.ResourceNotFoundException;
import com.backend.disp_cita_atencion.service.AtencionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AtencionController.class)
@Import(AtencionControllerTest.TestSecurityConfig.class)
public class AtencionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtencionService atencionService;

    @Autowired
    private ObjectMapper objectMapper;

    private AtencionResponseDTO atencionDTO1;
    private AtencionResponseDTO atencionDTO2;


    @BeforeEach
    void setUp() {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Para formatear Date como String

        TipoInsumoDTO tipoInsumoDTO = new TipoInsumoDTO(100L, "Medicamento", true);
        InsumoResponseDTO insumoDTO = new InsumoResponseDTO(101L, "Paracetamol", 10, new Date(), 500, true, tipoInsumoDTO);

        // Creamos una lista explícita para insumosServicio1
        List<InsumoResponseDTO> listaInsumosServicio1 = new ArrayList<>();
        listaInsumosServicio1.add(insumoDTO);
        // Línea corregida en AtencionControllerTest.java, dentro del método setUp():
        ServicioResponseDTO servicioDTO1 = new ServicioResponseDTO(201L, "Consulta Veterinaria", "Descripción de la consulta", 5000, true, listaInsumosServicio1);
        ServicioResponseDTO servicioDTO2 = new ServicioResponseDTO(202L, "Vacunación", "Descripción de la vacunación", 10000, true, Collections.emptyList());

        atencionDTO1 = new AtencionResponseDTO(1L, "Gripe", "Reposo", "Ninguna", new Date(), 15000, "veterinarioA", 101L, Arrays.asList(servicioDTO1, servicioDTO2));
        atencionDTO2 = new AtencionResponseDTO(2L, "Fractura", "Cirugía", "Complejo", new Date(), 50000, "veterinarioB", 102L, Arrays.asList(servicioDTO1));
    }

    // --- Pruebas para GET /api/atencion (obtener todas las atenciones activas) ---
    @Test
    @DisplayName("GET /api/atencion - Con rol ADMIN, debería retornar una lista de atenciones activas")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAll_ConAdmin_RetornaListaAtenciones() throws Exception {
        List<AtencionResponseDTO> atencionesActivas = Arrays.asList(atencionDTO1);
        when(atencionService.obtenerAtenciones()).thenReturn(atencionesActivas);

        mockMvc.perform(get("/api/atencion")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Listado exitoso"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].diagnostico").value("Gripe"));

        verify(atencionService, times(1)).obtenerAtenciones();
    }

    @Test
    @DisplayName("GET /api/atencion - Sin autenticación, debería retornar 401 Unauthorized")
    void testGetAll_SinAutenticacion_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/atencion")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(atencionService, never()).obtenerAtenciones();
    }

    @Test
    @DisplayName("GET /api/atencion - Con rol no autorizado (ej. USER), debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetAll_ConRolNoAutorizado_Forbidden() throws Exception {
        mockMvc.perform(get("/api/atencion")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(atencionService, never()).obtenerAtenciones();
    }

    // --- Pruebas para GET /api/atencion/{id} (buscar por ID) ---
    @Test
    @DisplayName("GET /api/atencion/{id} - Con rol VETERINARIO y ID existente, debería retornar la atención")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testGetById_Existente_RetornaAtencion() throws Exception {
        when(atencionService.buscarAtencionPorId(1L)).thenReturn(atencionDTO1);

        mockMvc.perform(get("/api/atencion/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.diagnostico").value("Gripe"));

        verify(atencionService, times(1)).buscarAtencionPorId(1L);
    }

    @Test
    @DisplayName("GET /api/atencion/{id} - Con rol ASISTENTE y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testGetById_NoExistente_RetornaNotFound() throws Exception {
        when(atencionService.buscarAtencionPorId(99L)).thenThrow(new ResourceNotFoundException("Atención no encontrada"));

        mockMvc.perform(get("/api/atencion/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Atención no encontrada"));

        verify(atencionService, times(1)).buscarAtencionPorId(99L);
    }

    // --- Pruebas para POST /api/atencion (crear atención) ---
    @Test
    @DisplayName("POST /api/atencion - Con rol ADMIN, debería crear una atención y retornar 201 Created")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate_ConAdmin_RetornaCreado() throws Exception {
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico nuevo", "Tratamiento nuevo", "Obs", new Date(), 20000, "veterinarioX", 103L, Collections.emptyList());
        AtencionResponseDTO createdResponseDTO = new AtencionResponseDTO(3L, requestDTO.getDiagnostico(), requestDTO.getTratamiento(), requestDTO.getObservaciones(), requestDTO.getFecha(), requestDTO.getTotalCosto(), requestDTO.getUsernameKeycloak(), requestDTO.getCitaId(), Collections.emptyList());

        when(atencionService.crearAtencion(any(AtencionRequestDTO.class))).thenReturn(createdResponseDTO);

        mockMvc.perform(post("/api/atencion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Creado correctamente"))
                .andExpect(jsonPath("$.data.id").value(3L))
                .andExpect(jsonPath("$.data.diagnostico").value("Diagnostico nuevo"));

        verify(atencionService, times(1)).crearAtencion(any(AtencionRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/atencion - Debería retornar 404 Not Found si la cita no existe")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testCreate_CitaNoEncontrada_NotFound() throws Exception {
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 99L, Collections.emptyList());
        when(atencionService.crearAtencion(any(AtencionRequestDTO.class))).thenThrow(new ResourceNotFoundException("Cita no encontrada"));

        mockMvc.perform(post("/api/atencion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Cita no encontrada"));

        verify(atencionService, times(1)).crearAtencion(any(AtencionRequestDTO.class));
    }

    // --- Pruebas para PUT /api/atencion/{id} (actualizar atención) ---
    @Test
    @DisplayName("PUT /api/atencion/{id} - Con rol ASISTENTE y ID existente, debería actualizar la atención")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testUpdate_ConAsistente_RetornaActualizado() throws Exception {
        Long idToUpdate = 1L;
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico Actualizado", "Tratamiento Actualizado", "Obs Actualizadas", new Date(), 18000, "veterinarioA", 101L, Collections.emptyList());
        AtencionResponseDTO updatedResponseDTO = new AtencionResponseDTO(idToUpdate, requestDTO.getDiagnostico(), requestDTO.getTratamiento(), requestDTO.getObservaciones(), requestDTO.getFecha(), requestDTO.getTotalCosto(), requestDTO.getUsernameKeycloak(), requestDTO.getCitaId(), Collections.emptyList());

        when(atencionService.actualizarAtencion(eq(idToUpdate), any(AtencionRequestDTO.class))).thenReturn(updatedResponseDTO);

        mockMvc.perform(put("/api/atencion/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data.diagnostico").value("Diagnostico Actualizado"))
                .andExpect(jsonPath("$.data.tratamiento").value("Tratamiento Actualizado"));

        verify(atencionService, times(1)).actualizarAtencion(eq(idToUpdate), any(AtencionRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/atencion/{id} - Con rol ADMIN y ID no existente, debería retornar 404 Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdate_AtencionNoEncontrada_RetornaNotFound() throws Exception {
        Long idToUpdate = 99L;
        AtencionRequestDTO requestDTO = new AtencionRequestDTO("Diagnostico", "Tratamiento", "Obs", new Date(), 1000, "user", 1L, Collections.emptyList());
        when(atencionService.actualizarAtencion(eq(idToUpdate), any(AtencionRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Atención no encontrada"));

        mockMvc.perform(put("/api/atencion/{id}", idToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(atencionService, times(1)).actualizarAtencion(eq(idToUpdate), any(AtencionRequestDTO.class));
    }

    // --- Pruebas para DELETE /api/atencion/{id} (eliminar atención - cambia estado a false) ---
    @Test
    @DisplayName("DELETE /api/atencion/{id} - Con rol VETERINARIO, debería eliminar (desactivar) la atención")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testDelete_ConVeterinario_Exito() throws Exception {
        Long idToDelete = 1L;
        doNothing().when(atencionService).eliminarAtencion(idToDelete);

        mockMvc.perform(delete("/api/atencion/{id}", idToDelete))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.message").value("Eliminado correctamente"));

        verify(atencionService, times(1)).eliminarAtencion(idToDelete);
    }

    @Test
    @DisplayName("DELETE /api/atencion/{id} - Con rol ASISTENTE y ID no encontrado, debería retornar 404 Not Found")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testDelete_NoEncontrado_RetornaNotFound() throws Exception {
        Long idToDelete = 99L;
        doThrow(new ResourceNotFoundException("Atención no encontrada para eliminar"))
                .when(atencionService).eliminarAtencion(idToDelete);

        mockMvc.perform(delete("/api/atencion/{id}", idToDelete))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));

        verify(atencionService, times(1)).eliminarAtencion(idToDelete);
    }

    // --- Pruebas para GET /api/atencion/por-usuario/{usernameKeycloak} ---
    @Test
    @DisplayName("GET /api/atencion/por-usuario/{username} - Debería retornar atenciones por username y estado (si se especifica)")
    @WithMockUser(username = "veterinario", roles = {"VETERINARIO"})
    void testBuscarPorUsuario_ConEstado_Exito() throws Exception {
        String username = "veterinarioA";
        List<AtencionResponseDTO> atencionesFiltradas = Collections.singletonList(atencionDTO1);
        when(atencionService.buscarAtencionesPorUsuario(username, true)).thenReturn(atencionesFiltradas);

        mockMvc.perform(get("/api/atencion/por-usuario/{username}", username)
                .param("estado", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].usernameKeycloak").value(username))
                .andExpect(jsonPath("$.data[0].diagnostico").value("Gripe"));

        verify(atencionService, times(1)).buscarAtencionesPorUsuario(username, true);
    }

    @Test
    @DisplayName("GET /api/atencion/por-usuario/{username} - Debería retornar todas las atenciones por username si estado no se especifica")
    @WithMockUser(username = "asistente", roles = {"ASISTENTE"})
    void testBuscarPorUsuario_SinEstado_Exito() throws Exception {
        String username = "veterinarioA";
        List<AtencionResponseDTO> allAtenciones = Arrays.asList(atencionDTO1, atencionDTO2); // Suponiendo que atencionDTO2 sea de veterinarioA para este test
        when(atencionService.buscarAtencionesPorUsuario(username, null)).thenReturn(allAtenciones);

        mockMvc.perform(get("/api/atencion/por-usuario/{username}", username)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exito"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].usernameKeycloak").value(username));

        verify(atencionService, times(1)).buscarAtencionesPorUsuario(username, null);
    }

    @Test
    @DisplayName("GET /api/atencion/por-usuario/{username} - Sin rol autorizado, debería retornar 403 Forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testBuscarPorUsuario_SinRolAutorizado_Forbidden() throws Exception {
        mockMvc.perform(get("/api/atencion/por-usuario/{username}", "anyuser")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        verify(atencionService, never()).buscarAtencionesPorUsuario(anyString(), anyBoolean());
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
                    .requestMatchers(HttpMethod.GET, "/api/atencion", "/api/atencion/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.POST, "/api/atencion").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.PUT, "/api/atencion/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.DELETE, "/api/atencion/{id}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
                    .requestMatchers(HttpMethod.GET, "/api/atencion/por-usuario/{usernameKeycloak}").hasAnyRole("ADMIN", "VETERINARIO", "ASISTENTE")
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
