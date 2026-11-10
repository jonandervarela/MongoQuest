
package com.mycompany.reto1_erandio_academy.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaMenuEmpleado {

    public void mostrar() {

        Stage ventana = new Stage();

        Label titulo = new Label("PANEL DE EMPLEADO");

        titulo.setStyle(
                "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
        );

        Button botonActividades =
                new Button("Gestionar actividades");

        Button botonTareas =
                new Button("Gestionar tareas");

        Button botonTemario =
                new Button("Consultar temario");

        Button botonSalir =
                new Button("Cerrar sesión");

        botonActividades.setOnAction(e ->
                new VentanaActividades().mostrar()
        );

        botonTareas.setOnAction(e ->
                new VentanaTareas().mostrar()
        );

        botonTemario.setOnAction(e ->
                new VentanaTemarioUsuario().mostrar()
        );

        botonSalir.setOnAction(e -> ventana.close());

        VBox contenido = new VBox(
                20,
                titulo,
                botonActividades,
                botonTareas,
                botonTemario,
                botonSalir
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Scene escena = new Scene(contenido, 500, 450);

        ventana.setTitle("Academy - Empleado");
        ventana.setScene(escena);
        ventana.show();
    }
}
