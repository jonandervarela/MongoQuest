
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Actividad {

    private ObjectId id;
    private String titulo;
    private String descripcion;
    private ObjectId cursoId;

    public Actividad() {
    }

    public Actividad(ObjectId id, String titulo,
            String descripcion, ObjectId cursoId) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cursoId = cursoId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ObjectId getCursoId() {
        return cursoId;
    }

    public void setCursoId(ObjectId cursoId) {
        this.cursoId = cursoId;
    }
}
