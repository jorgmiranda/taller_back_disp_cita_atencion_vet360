package com.backend.disp_cita_atencion.dto.response;


import java.util.Date;

public class InsumoResponseDTO {
    private Long id;
    private String nombre;
    private Integer cantidadUsada;
    private Date fechaVencimiento;
    private Integer precioUnitario;
    private Boolean estado;
    private TipoInsumoDTO tipoInsumo;

    public InsumoResponseDTO(Long id, String nombre, Integer cantidadUsada, Date fechaVencimiento, Integer precioUnitario,
            Boolean estado, TipoInsumoDTO tipoInsumo) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadUsada = cantidadUsada;
        this.fechaVencimiento = fechaVencimiento;
        this.precioUnitario = precioUnitario;
        this.estado = estado;
        this.tipoInsumo = tipoInsumo;
    }

    public InsumoResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidadUsada() {
        return cantidadUsada;
    }

    public void setCantidadUsada(Integer cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Integer precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public TipoInsumoDTO getTipoInsumo() {
        return tipoInsumo;
    }

    public void setTipoInsumo(TipoInsumoDTO tipoInsumo) {
        this.tipoInsumo = tipoInsumo;
    }

    

}
