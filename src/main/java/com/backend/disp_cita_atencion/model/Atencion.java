package com.backend.disp_cita_atencion.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "atencion")
public class Atencion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atencion")
    private Long idAtencion;

    @Column(nullable = false, length = 255)
    private String diagnostico;

    @Column(nullable = false, length = 255)
    private String tratamiento;

    @Column(length = 255)
    private String observaciones;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date fecha;

    @Column(name = "total_costo", nullable = false)
    private Integer totalCosto;

    @Column(name = "username_keycloak", nullable = false, length = 100)
    private String usernameKeycloak;

    @Column(nullable = false)
    private Boolean estado = true;

    @OneToOne
    @JoinColumn(name = "cita_id_cita", nullable = false, unique = true)
    private Cita cita;

    @ManyToMany
    @JoinTable(name = "servicio_atencion", joinColumns = @JoinColumn(name = "atencion_id_atencion"), inverseJoinColumns = @JoinColumn(name = "servicio_id_servicio"))
    private List<Servicio> servicios;

    public Atencion() {
    }

    public Atencion(Long idAtencion, String diagnostico, String tratamiento, String observaciones, Date fecha,
            Integer totalCosto, String usernameKeycloak, Boolean estado, Cita cita, List<Servicio> servicios) {
        this.idAtencion = idAtencion;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.totalCosto = totalCosto;
        this.usernameKeycloak = usernameKeycloak;
        this.estado = estado;
        this.cita = cita;
        this.servicios = servicios;
    }

    public Long getIdAtencion() {
        return idAtencion;
    }

    public void setIdAtencion(Long idAtencion) {
        this.idAtencion = idAtencion;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getTotalCosto() {
        return totalCosto;
    }

    public void setTotalCosto(Integer totalCosto) {
        this.totalCosto = totalCosto;
    }

    public String getUsernameKeycloak() {
        return usernameKeycloak;
    }

    public void setUsernameKeycloak(String usernameKeycloak) {
        this.usernameKeycloak = usernameKeycloak;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public List<Servicio> getServicios() {
        return servicios;
    }

    public void setServicios(List<Servicio> servicios) {
        this.servicios = servicios;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

}
