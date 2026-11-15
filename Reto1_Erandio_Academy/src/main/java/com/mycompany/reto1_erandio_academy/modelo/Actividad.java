package com.mycompany.reto1_erandio_academy.modelo;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Actividad {

    private ObjectId id;
    private String titulo;
    private String descripcion;
    private ObjectId cursoId;

    // NUEVO: ejercicios embebidos (antes siempre se guardaba vacío)
    private List<Ejercicio> ejercicios;

    public Actividad() {
        this.ejercicios = new ArrayList<>();
    }

    public Actividad(ObjectId id, String titulo,
            String descripcion, ObjectId cursoId) {

        this(id, titulo, descripcion, cursoId, new ArrayList<>());
    }

    // NUEVO: constructor con ejercicios
    public Actividad(ObjectId id, String titulo,
            String descripcion, ObjectId cursoId,
            List<Ejercicio> ejercicios) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cursoId = cursoId;
        this.ejercicios = (ejercicios == null)
                ? new ArrayList<>() : ejercicios;
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

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = (ejercicios == null)
                ? new ArrayList<>() : ejercicios;
    }
}
