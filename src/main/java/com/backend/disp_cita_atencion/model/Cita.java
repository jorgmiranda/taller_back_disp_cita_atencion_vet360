package com.backend.disp_cita_atencion.model;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "cita")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Long idCita;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_hora_inicio", nullable = false)
    private Date fechaHoraInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_hora_fin")
    private Date fechaHoraFin;

    @Column(length = 25)
    private String estado;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "username_keycloak", nullable = false, length = 30)
    private String usernameKeycloak;

    @ManyToOne
    @JoinColumn(name = "disponibilidad_id_disponibilidad", nullable = false)
    private Disponibilidad disponibilidad;

    @ManyToOne
    @JoinColumn(name = "mascota_id_mascota", nullable = false)
    private Mascota mascota;

    @OneToOne(mappedBy = "cita")
    private Atencion atencion;

    public Cita() {
    }

    public Cita(Long idCita, Date fechaHoraInicio, Date fechaHoraFin, String estado,
            String motivo, String usernameKeycloak, Disponibilidad disponibilidad,
            Mascota mascota, Atencion atencion) {
        this.idCita = idCita;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
        this.motivo = motivo;
        this.usernameKeycloak = usernameKeycloak;
        this.disponibilidad = disponibilidad;
        this.mascota = mascota;
        this.atencion = atencion;
    }

    public Long getIdCita() {
        return idCita;
    }

    public void setIdCita(Long idCita) {
        this.idCita = idCita;
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

    public Disponibilidad getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Disponibilidad disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        this.mascota = mascota;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

}
