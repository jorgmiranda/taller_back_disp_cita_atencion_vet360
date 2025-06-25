package com.backend.disp_cita_atencion.dto.response;


import java.util.Date;

public class MascotaDTO {
    private Long id;
    private String chip;
    private String nombre;
    private Date fechaNacimiento;
    private String genero;
    private Boolean estado;
    private Long razaId;
    private Long duenoId;

    public MascotaDTO() {
    }

    public MascotaDTO(Long id, String chip, String nombre, Date fechaNacimiento, String genero, Boolean estado,
            Long razaId, Long duenoId) {
        this.id = id;
        this.chip = chip;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.estado = estado;
        this.razaId = razaId;
        this.duenoId = duenoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Long getRazaId() {
        return razaId;
    }

    public void setRazaId(Long razaId) {
        this.razaId = razaId;
    }

    public Long getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(Long duenoId) {
        this.duenoId = duenoId;
    }

}
