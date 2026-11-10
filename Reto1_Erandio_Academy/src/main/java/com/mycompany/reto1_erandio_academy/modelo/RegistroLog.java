
package com.mycompany.reto1_erandio_academy.modelo;

import java.util.Date;
import org.bson.types.ObjectId;

public class RegistroLog {

    private ObjectId id;
    private Date fecha;
    private String usuario;
    private String operacion;
    private String coleccion;
    private String resultado;
    private String error;

    public RegistroLog(
            Date fecha,
            String usuario,
            String operacion,
            String coleccion,
            String resultado,
            String error) {

        this.fecha = fecha;
        this.usuario = usuario;
        this.operacion = operacion;
        this.coleccion = coleccion;
        this.resultado = resultado;
        this.error = error;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getOperacion() {
        return operacion;
    }

    public String getColeccion() {
        return coleccion;
    }

    public String getResultado() {
        return resultado;
    }

    public String getError() {
        return error;
    }
}
