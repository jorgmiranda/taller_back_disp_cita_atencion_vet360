package com.backend.disp_cita_atencion.dto.response;


import java.util.Date;

public class DisponibilidadResponseDTO {
    private Long id;
    private Date fecha;
    private String horaInicio;
    private String horaFin;
    private Boolean disponible;
    private String usernameKeycloak;

    public DisponibilidadResponseDTO() {
    }

    public DisponibilidadResponseDTO(Long id, Date fecha, String horaInicio, String horaFin, Boolean disponible,
            String usernameKeycloak) {
        this.id = id;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.disponible = disponible;
        this.usernameKeycloak = usernameKeycloak;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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
