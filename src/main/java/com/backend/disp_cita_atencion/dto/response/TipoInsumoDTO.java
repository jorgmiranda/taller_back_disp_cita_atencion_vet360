package com.backend.disp_cita_atencion.dto.response;

public class TipoInsumoDTO {
    private Long id;
    private String nombreTipo;
    private Boolean estado;

    // Constructor vacío (si lo usas para deserialización de JSON)
    public TipoInsumoDTO() {
    }

    // Constructor con todos los campos - ¡AÑADE ESTE!
    public TipoInsumoDTO(Long id, String nombreTipo, Boolean estado) {
        this.id = id;
        this.nombreTipo = nombreTipo;
        this.estado = estado;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

}
