
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Valoracion {

    private ObjectId id;
    private ObjectId usuarioId;
    private ObjectId cursoId;
    private int puntuacion;
    private String comentario;

    public Valoracion() {
    }

    public Valoracion(ObjectId usuarioId, ObjectId cursoId,
                      int puntuacion, String comentario) {
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    public Valoracion(ObjectId id, ObjectId usuarioId, ObjectId cursoId,
                      int puntuacion, String comentario) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
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

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
