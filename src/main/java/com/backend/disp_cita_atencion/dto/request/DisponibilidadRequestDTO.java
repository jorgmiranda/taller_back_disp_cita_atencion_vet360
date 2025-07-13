package com.backend.disp_cita_atencion.dto.request;

import java.time.LocalDate;

public class DisponibilidadRequestDTO {
    private LocalDate fecha;
    private String horaInicio;
    private String horaFin;
    private Boolean disponible;
    private String usernameKeycloak;

    public DisponibilidadRequestDTO() {
    }

    public DisponibilidadRequestDTO(LocalDate fecha, String horaInicio, String horaFin, Boolean disponible,
            String usernameKeycloak) {
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
        this.usernameKeycloak = usernameKeycloak;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getUsernameKeycloak() {
        return usernameKeycloak;
    }

    public void setUsernameKeycloak(String usernameKeycloak) {
        this.usernameKeycloak = usernameKeycloak;
    }

}
