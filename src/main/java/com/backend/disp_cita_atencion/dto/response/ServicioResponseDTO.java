package com.backend.disp_cita_atencion.dto.response;


import java.util.List;

public class ServicioResponseDTO {
    private Long id;
    private String nombreServicio;
    private String descripcionServicio;
    private Integer precio;
    private Boolean estado;
    private List<InsumoResponseDTO> insumos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio) {
        this.descripcionServicio = descripcionServicio;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public List<InsumoResponseDTO> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<InsumoResponseDTO> insumos) {
        this.insumos = insumos;
    }

    public ServicioResponseDTO(Long id, String nombreServicio, String descripcionServicio,
                           Integer precio, Boolean estado, List<InsumoResponseDTO> insumos) {
        this.id = id;
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.precio = precio;
        this.estado = estado;
        this.insumos = insumos;
    }

    public ServicioResponseDTO() {
        // Constructor por defecto
    }
}
