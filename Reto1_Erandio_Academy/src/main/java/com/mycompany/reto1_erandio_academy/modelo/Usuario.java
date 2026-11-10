
package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

public class Usuario {

    private ObjectId id;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String rol;
    private ObjectId empleadoId;

    public Usuario() {
    }

    public Usuario(ObjectId id, String nombre, String email,
            String password, String telefono) {

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.rol = "USUARIO";
        this.empleadoId = null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public ObjectId getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(ObjectId empleadoId) {
        this.empleadoId = empleadoId;
    }
}
