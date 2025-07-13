package com.backend.disp_cita_atencion.config;

import java.util.Arrays; // Importa Arrays
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Importa SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter; // <-- ¡Nueva importación!


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita @PreAuthorize en tus controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs REST
            .authorizeHttpRequests(authorize -> authorize
                // Puedes permitir rutas públicas aquí si es necesario (ej. /actuator/health)
                // .requestMatchers("/public/**", "/actuator/health").permitAll()
                // Todas las demás peticiones requieren autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Configura el conversor de roles
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // APIs REST no mantienen estado de sesión
            )
            .cors(cors -> cors.configurationSource(corsConfigurationSource())); // Usa tu bean de CORS

        return http.build();
    }

    // --- Converter para extraer roles de Keycloak del JWT y mapearlos a Spring Security ---
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter()); // Usará la clase que crearemos
        return converter;
    }

    // --- Configuración de CORS (tu bean actual es bueno, solo lo ajustamos para que se use explícitamente) ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos - Ajusta esto para tu frontend real en producción
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "https://funny-alfajores-7e9e6e.netlify.app")); // O Collections.singletonList("http://localhost:4200");
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Encabezados permitidos - Importante incluir Authorization para JWTs
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With")); // Agregué X-Requested-With
        // Permite el envío de credenciales (como el encabezado Authorization)
        configuration.setAllowCredentials(true);
        // Tiempo que los resultados de preflight pueden ser cacheados
        configuration.setMaxAge(3600L); // 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas las rutas
        return source;
    }

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para facilitar pruebas desde Angular
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Permite todo sin autenticación
            )
            .httpBasic(Customizer.withDefaults()); // No requiere autenticación básica, pero evita errores

        return http.build();
    }
    */
}

