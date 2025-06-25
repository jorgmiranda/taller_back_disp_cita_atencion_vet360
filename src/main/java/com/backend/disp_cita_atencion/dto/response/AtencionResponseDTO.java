package com.backend.disp_cita_atencion.dto.response;

import java.util.Date;
import java.util.List;

public class AtencionResponseDTO {
    private Long id;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Date fecha;
    private Integer totalCosto;
    private String usernameKeycloak;
    private Long citaId;
    private List<ServicioResponseDTO> servicios;

    public AtencionResponseDTO() {
    }

    public AtencionResponseDTO(Long id, String diagnostico, String tratamiento, String observaciones, Date fecha,
            Integer totalCosto, String usernameKeycloak, Long citaId, List<ServicioResponseDTO> servicios) {
        this.id = id;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.totalCosto = totalCosto;
        this.usernameKeycloak = usernameKeycloak;
        this.citaId = citaId;
        this.servicios = servicios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public List<ServicioResponseDTO> getServicios() {
        return servicios;
    }

    public void setServicios(List<ServicioResponseDTO> servicios) {
        this.servicios = servicios;
    }

}
