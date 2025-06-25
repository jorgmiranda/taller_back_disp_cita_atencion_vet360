package com.backend.disp_cita_atencion.dto.request;


import java.util.Date;
import java.util.List;

public class AtencionRequestDTO {
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Date fecha;
    private Integer totalCosto;
    private String usernameKeycloak;
    private Long citaId;
    private List<Long> servicioIds;

    public AtencionRequestDTO() {
    }

    public AtencionRequestDTO(String diagnostico, String tratamiento, String observaciones, Date fecha,
            Integer totalCosto, String usernameKeycloak, Long citaId, List<Long> servicioIds) {
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.totalCosto = totalCosto;
        this.usernameKeycloak = usernameKeycloak;
        this.citaId = citaId;
        this.servicioIds = servicioIds;
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

    public List<Long> getServicioIds() {
        return servicioIds;
    }

    public void setServicioIds(List<Long> servicioIds) {
        this.servicioIds = servicioIds;
    }

}
