
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Matricula {

    private ObjectId id;
    private ObjectId usuarioId;
    private ObjectId cursoId;
    private String fecha;
    private String estado;

    public Matricula() {
    }

    public Matricula(ObjectId id, ObjectId usuarioId,
            ObjectId cursoId, String fecha, String estado) {

        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.fecha = fecha;
        this.estado = estado;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(ObjectId usuarioId) {
        this.usuarioId = usuarioId;
    }

    public ObjectId getCursoId() {
        return cursoId;
    }

    public void setCursoId(ObjectId cursoId) {
        this.cursoId = cursoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
