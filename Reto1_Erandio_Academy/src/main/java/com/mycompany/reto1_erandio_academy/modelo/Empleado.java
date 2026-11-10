package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Empleado {

    private ObjectId id;
    private String nombre;
    private String puesto;
    private String departamento;
    private boolean activo;

    public Empleado() {
    }

    public Empleado(
            ObjectId id,
            String nombre,
            String puesto,
            String departamento,
            boolean activo) {

        this.id = id;
        this.nombre = nombre;
        this.puesto = puesto;
        this.departamento = departamento;
        this.activo = activo;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}