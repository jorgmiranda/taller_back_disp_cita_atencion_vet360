package com.backend.disp_cita_atencion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "raza")
public class Raza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_raza")
    private Long idRaza;

    @Column(name = "nombre_raza", nullable = false, length = 100)
    private String nombreRaza;

    @ManyToOne
    @JoinColumn(name = "especie_id_especie", nullable = false)
    private Especie especie;

    public Raza() {
    }

    public Raza(Long idRaza, String nombreRaza, Especie especie) {
        this.idRaza = idRaza;
        this.nombreRaza = nombreRaza;
        this.especie = especie;
    }

    public Long getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(Long idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }
}
