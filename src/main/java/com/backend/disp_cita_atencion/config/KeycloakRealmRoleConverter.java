package com.backend.disp_cita_atencion.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream; // Importa Stream

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Inicializa un stream vacío para acumular las autoridades
        Stream<GrantedAuthority> authorities = Stream.empty();

        // 1. Intentar extraer roles del 'realm_access' (roles de realm)
        Object realmAccessObj = jwt.getClaims().get("realm_access");
        Map<String, Object> realmAccess = null;
        if (realmAccessObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) realmAccessObj;
            realmAccess = casted;
        }
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof List<?>) {
                List<?> rolesList = (List<?>) rolesObj;
                List<String> realmRoles = rolesList.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList());
                authorities = Stream.concat(authorities, realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                );
            }
        }

        // 2. Intentar extraer roles de 'resource_access' (roles de cliente)
        // Reemplaza 'vetcare-app-service' con el ID real de tu cliente en Keycloak
        // O con 'vetcare-app' si ese es el Client ID configurado para el microservicio
        Object resourceAccessObj = jwt.getClaims().get("resource_access");
        Map<String, Object> resourceAccess = null;
        if (resourceAccessObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) resourceAccessObj;
            resourceAccess = casted;
        }
        if (resourceAccess != null) {
            Object clientAccessObj = resourceAccess.get("vetcare-app"); // <-- ¡Revisa este Client ID!
            Map<String, Object> clientAccess = null;
            if (clientAccessObj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Object> castedClient = (Map<String, Object>) clientAccessObj;
                clientAccess = castedClient;
            }
            if (clientAccess != null && clientAccess.containsKey("roles")) {
                Object clientRolesObj = clientAccess.get("roles");
                if (clientRolesObj instanceof List<?>) {
                    List<?> clientRolesList = (List<?>) clientRolesObj;
                    List<String> clientRoles = clientRolesList.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .collect(Collectors.toList());
                    authorities = Stream.concat(authorities, clientRoles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    );
                }
            }
        }
        
        // Puedes añadir más fuentes de roles si tu Keycloak los organiza de otra manera.

        return authorities.collect(Collectors.toList());
    }



}
