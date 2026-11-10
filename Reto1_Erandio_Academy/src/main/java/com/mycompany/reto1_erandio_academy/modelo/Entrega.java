
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Entrega {

    private ObjectId id;
    private ObjectId tareaId;
    private ObjectId usuarioId;
    private String estado;
    private String fechaEntrega;
    private String nombreArchivo;
    private String rutaArchivo;

    public Entrega() {
    }

    public Entrega(ObjectId id, ObjectId tareaId,
            ObjectId usuarioId, String estado,
            String fechaEntrega, String nombreArchivo,
            String rutaArchivo) {

        this.id = id;
        this.tareaId = tareaId;
        this.usuarioId = usuarioId;
        this.estado = estado;
        this.fechaEntrega = fechaEntrega;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
    }

    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getTareaId() { return tareaId; }
    public void setTareaId(ObjectId tareaId) { this.tareaId = tareaId; }

    public ObjectId getUsuarioId() { return usuarioId; }
    public void setUsuarioId(ObjectId usuarioId) { this.usuarioId = usuarioId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
