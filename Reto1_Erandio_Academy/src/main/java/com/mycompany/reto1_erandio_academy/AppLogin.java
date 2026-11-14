/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.reto1_erandio_academy;

import com.mycompany.reto1_erandio_academy.vista.VentanaLogin;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 *
 * @author jonan
 */

public class AppLogin extends Application {

    @Override
    public void start(Stage escenarioPrincipal) {

        // VentanaLogin crea su propio Stage, así que el principal no se usa
        new VentanaLogin().mostrar();
    }
}


