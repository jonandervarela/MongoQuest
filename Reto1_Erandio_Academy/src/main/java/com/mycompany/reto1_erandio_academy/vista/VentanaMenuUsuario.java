
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaMenuUsuario {

    public void mostrar() {

        Stage ventana = new Stage();

        Label titulo = new Label("PANEL DE USUARIO");

        titulo.setStyle(
                "-fx-font-size: 26px;"
                + "-fx-font-weight: bold;"
        );

        Button botonTemario = new Button("Consultar temario");
        Button botonPerfil = new Button("Actualizar mi perfil");
        Button botonValoraciones = new Button("Valorar cursos");
        Button botonMatriculas = new Button("Consultar mis matrículas");
        Button botonBaja = new Button("Darme de baja de un curso");
        Button botonEliminar = new Button("Eliminar mi cuenta");
        Button botonSalir = new Button("Cerrar sesión");

        botonTemario.setOnAction(e ->
                new VentanaTemarioUsuario().mostrar()
        );

        botonPerfil.setOnAction(e ->
                new VentanaPerfilUsuario().mostrar()
        );

        botonValoraciones.setOnAction(e ->
                new VentanaValoraciones().mostrar()
        );

        botonMatriculas.setOnAction(e ->
                new VentanaMatriculas().mostrar()
        );

        botonBaja.setOnAction(e ->
                new VentanaBajaMatricula().mostrar()
        );

        botonEliminar.setOnAction(e ->
                new VentanaEliminarCuenta().mostrar()
        );

        botonSalir.setOnAction(e -> {
            SesionUsuario.cerrarSesion();
            ventana.close();
            new VentanaLogin().mostrar();
        });

        VBox contenido = new VBox(
                15,
                titulo,
                botonTemario,
                botonPerfil,
                botonValoraciones,
                botonMatriculas,
                botonBaja,
                botonEliminar,
                botonSalir
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        Scene escena = new Scene(contenido, 500, 550);

        ventana.setTitle("Academy - Usuario");
        ventana.setScene(escena);
        ventana.show();
    }
}
