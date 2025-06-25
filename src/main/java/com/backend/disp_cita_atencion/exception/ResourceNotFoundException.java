package com.backend.disp_cita_atencion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Excepción personalizada que se lanza cuando un recurso solicitado no se encuentra en la base de datos.
 * Al estar anotada con @ResponseStatus(HttpStatus.NOT_FOUND), retorna automáticamente un código 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

