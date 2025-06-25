package com.backend.disp_cita_atencion.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "especie")
public class Especie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especie")
    private Long idEspecie;

    @Column(name = "nombre_especie", nullable = false, length = 100)
    private String nombreEspecie;

    @Column(nullable = false)
    private Boolean estado;

    @OneToMany(mappedBy = "especie")
    private List<Raza> razas;

    public Especie() {
    }

    public Especie(Long idEspecie, String nombreEspecie, Boolean estado) {
        this.idEspecie = idEspecie;
        this.nombreEspecie = nombreEspecie;
        this.estado = estado;
    }

    public Long getIdEspecie() {
        return idEspecie;
    }

    public void setIdEspecie(Long idEspecie) {
        this.idEspecie = idEspecie;
    }

    public String getNombreEspecie() {
        return nombreEspecie;
    }

    public void setNombreEspecie(String nombreEspecie) {
        this.nombreEspecie = nombreEspecie;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public List<Raza> getRazas() {
        return razas;
    }

    public void setRazas(List<Raza> razas) {
        this.razas = razas;
    }
}
