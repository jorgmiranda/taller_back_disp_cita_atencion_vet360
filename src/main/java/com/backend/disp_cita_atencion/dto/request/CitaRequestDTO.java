package com.backend.disp_cita_atencion.dto.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CitaRequestDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date fechaHoraInicio;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date fechaHoraFin;
    private String estado;
    private String motivo;
    private String usernameKeycloak;
    private Long disponibilidadId;
    private Long mascotaId;

    public CitaRequestDTO() {
    }

    public CitaRequestDTO(Date fechaHoraInicio, Date fechaHoraFin, String estado, String motivo,
            String usernameKeycloak, Long disponibilidadId, Long mascotaId) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.motivo = motivo;
        this.usernameKeycloak = usernameKeycloak;
        this.disponibilidadId = disponibilidadId;
        this.mascotaId = mascotaId;
    }

    public Date getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(Date fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Date getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(Date fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getUsernameKeycloak() {
        return usernameKeycloak;
    }

    public void setUsernameKeycloak(String usernameKeycloak) {
        this.usernameKeycloak = usernameKeycloak;
    }

    public Long getDisponibilidadId() {
        return disponibilidadId;
    }

    public void setDisponibilidadId(Long disponibilidadId) {
        this.disponibilidadId = disponibilidadId;
    }

    public Long getMascotaId() {
        return mascotaId;
    }

    public void setMascotaId(Long mascotaId) {
        this.mascotaId = mascotaId;
    }

}
