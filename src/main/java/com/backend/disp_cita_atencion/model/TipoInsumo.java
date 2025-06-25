package com.backend.disp_cita_atencion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_insumo")
public class TipoInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_insumo")
    private Long idTipoInsumo;

    @Column(name = "nombre_tipo", nullable = false, length = 100)
    private String nombreTipo;

    @Column(nullable = false)
    private Boolean estado;

    public TipoInsumo() {
    }

    public TipoInsumo(Long idTipoInsumo, String nombreTipo, Boolean estado) {
        this.idTipoInsumo = idTipoInsumo;
        this.nombreTipo = nombreTipo;
        this.estado = estado;
    }

    public Long getIdTipoInsumo() {
        return idTipoInsumo;
    }

    public void setIdTipoInsumo(Long idTipoInsumo) {
        this.idTipoInsumo = idTipoInsumo;
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
