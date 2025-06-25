package com.backend.disp_cita_atencion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
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
}

