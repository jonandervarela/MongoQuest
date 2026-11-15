package com.mycompany.reto1_erandio_academy.modelo;

/**
 * Ejercicio embebido dentro de una Actividad (Tema).
 * Solo lleva la pregunta, que es el único campo que exige
 * el validador de MongoDB para la colección "actividades".
 */
public class Ejercicio {

    private String pregunta;

    public Ejercicio() {
    }

    public Ejercicio(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    // Para que se vea bien dentro de una ListView
    @Override
    public String toString() {
        return pregunta;
    }
}
