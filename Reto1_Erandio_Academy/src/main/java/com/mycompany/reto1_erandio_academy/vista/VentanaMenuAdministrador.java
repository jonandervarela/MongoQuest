
package com.mycompany.reto1_erandio_academy.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

public class VentanaMenuAdministrador {

    public void mostrar() {

        Stage ventana = new Stage();

        Label titulo = new Label("PANEL DE ADMINISTRADOR");

        titulo.setStyle(
                "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
        );

        Button botonEmpleados =
                new Button("Gestionar empleados");

        Button botonTareas =
                new Button("Gestionar tareas");

        Button botonCursos =
                new Button("Gestionar cursos");

        Button botonAsignaciones =
                new Button("Asignar profesores");

        Button botonUsuarios =
                new Button("Gestionar usuarios");

        Button botonMatriculas =
                new Button("Gestionar matrículas");

        Button botonActividades =
                new Button("Gestionar actividades");

        // NUEVO BOTÓN
        Button botonEmpresa =
                new Button("Datos de la empresa");

        Button botonSalir =
                new Button("Cerrar sesión");

        // ACCIONES
        botonEmpleados.setOnAction(e ->
                new VentanaListaEmpleados().mostrar()
        );

        botonTareas.setOnAction(e ->
                new VentanaTareas().mostrar()
        );

        botonCursos.setOnAction(e ->
                new VentanaCursos().mostrar()
        );

        botonAsignaciones.setOnAction(e ->
                new VentanaAsignaciones().mostrar()
        );

        botonUsuarios.setOnAction(e ->
                new VentanaListaUsuarios().mostrar()
        );

        botonMatriculas.setOnAction(e ->
                new VentanaMatriculas().mostrar()
        );

        botonActividades.setOnAction(e ->
                new VentanaActividades().mostrar()
        );

        // ABRIR GESTIÓN DE EMPRESA
        botonEmpresa.setOnAction(e ->
                new VentanaEmpresa().mostrar()
        );

        // CERRAR SESIÓN
        botonSalir.setOnAction(e -> {
            SesionUsuario.cerrarSesion();
            ventana.close();
            new VentanaLogin().mostrar();
        });

        // CONTENIDO
        VBox contenido = new VBox(
                15,
                titulo,
                botonEmpleados,
                botonTareas,
                botonCursos,
                botonAsignaciones,
                botonUsuarios,
                botonMatriculas,
                botonActividades,
                botonEmpresa,
                botonSalir
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Scene escena = new Scene(contenido, 500, 720);

        ventana.setTitle("Academy - Administrador");
        ventana.setScene(escena);
        ventana.show();
    }
}
