/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reto1_erandio_academy.modelo;

/**
 *
 * @author jonan
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

