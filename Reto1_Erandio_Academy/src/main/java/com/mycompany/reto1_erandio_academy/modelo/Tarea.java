
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Tarea {

    private ObjectId id;
    private String titulo;
    private ObjectId profesorId;
    private ObjectId cursoId;
    private String fechaLimite;

    // Constructor vacío
    public Tarea() {
    }

    // Constructor completo
    public Tarea(ObjectId id, String titulo,
            ObjectId profesorId, ObjectId cursoId,
            String fechaLimite) {

        this.id = id;
        this.titulo = titulo;
        this.profesorId = profesorId;
        this.cursoId = cursoId;
        this.fechaLimite = fechaLimite;
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

    public ObjectId getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(ObjectId profesorId) {
        this.profesorId = profesorId;
    }

    public ObjectId getCursoId() {
        return cursoId;
    }

    public void setCursoId(ObjectId cursoId) {
        this.cursoId = cursoId;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
}
