package com.backend.disp_cita_atencion.dto.response;


import java.util.Date;

public class CitaResponseDTO {
    private Long id;
    private Date fechaHoraInicio;
    private Date fechaHoraFin;
    private String estado;
    private String motivo;
    private String usernameKeycloak;
    private DisponibilidadResponseDTO disponibilidad;
    private MascotaDTO mascota;

    public CitaResponseDTO() {
    }

    public CitaResponseDTO(Long id, Date fechaHoraInicio, Date fechaHoraFin, String estado, String motivo,
            String usernameKeycloak, DisponibilidadResponseDTO disponibilidad, MascotaDTO mascota) {
        this.id = id;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.motivo = motivo;
        this.usernameKeycloak = usernameKeycloak;
        this.disponibilidad = disponibilidad;
        this.mascota = mascota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DisponibilidadResponseDTO getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(DisponibilidadResponseDTO disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public MascotaDTO getMascota() {
        return mascota;
    }

    public void setMascota(MascotaDTO mascota) {
        this.mascota = mascota;
    }

}
