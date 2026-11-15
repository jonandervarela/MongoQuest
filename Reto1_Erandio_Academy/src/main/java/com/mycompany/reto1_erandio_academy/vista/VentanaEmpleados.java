
package com.mycompany.reto1_erandio_academy.vista;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaEmpleados extends Application {

    private static final String AZUL_OSCURO = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";

    @Override
    public void start(Stage escenario) {

        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        // CABECERA
        VBox cabecera = new VBox(8);
        cabecera.setPadding(new Insets(30));
        cabecera.setStyle(
                "-fx-background-color: " + AZUL_OSCURO + ";"
        );

        Label titulo = new Label("ERANDIO ACADEMY");
        titulo.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitulo = new Label(
                "Panel principal de gestión y administración"
        );
        subtitulo.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #CBD5E1;"
        );

        cabecera.getChildren().addAll(titulo, subtitulo);
        raiz.setTop(cabecera);

        // CONTENIDO
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(30));
        contenido.setMaxWidth(1000);

        Label tituloSeccion = new Label("Accesos de la aplicación");
        tituloSeccion.setStyle(
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";"
        );

        Label descripcion = new Label(
                "Selecciona una opción para abrir el módulo correspondiente."
        );
        descripcion.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748B;"
        );

        // BOTONES
        Button botonEmpleados = crearBoton("Gestionar empleados");
        Button botonTareas = crearBoton("Gestionar tareas");
        Button botonCursos = crearBoton("Gestionar cursos");
        Button botonAsignaciones = crearBoton("Asignar profesores");
        Button botonRegistrarUsuario = crearBoton("Registrar usuario");
        Button botonUsuarios = crearBoton("Gestionar usuarios");
        Button botonLogin = crearBoton("Iniciar sesión");
        Button botonMatriculas = crearBoton("Gestionar matrículas");
        Button botonActividades = crearBoton("Gestionar actividades");
        Button botonEliminarCuenta = crearBoton("Eliminar cuenta");
        Button botonTemario = crearBoton("Consultar temario");
        Button botonValoraciones = crearBoton("Valorar cursos");
        Button botonBajaMatricula = crearBoton("Darse de baja de un curso");
        Button botonPerfil = crearBoton("Actualizar perfil");

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

        botonRegistrarUsuario.setOnAction(e ->
                new VentanaRegistroUsuario().mostrar()
        );

        botonUsuarios.setOnAction(e ->
                new VentanaListaUsuarios().mostrar()
        );

        botonLogin.setOnAction(e ->
                new VentanaLogin().mostrar()
        );

        botonMatriculas.setOnAction(e ->
                new VentanaMatriculas().mostrar()
        );

        botonActividades.setOnAction(e ->
                new VentanaActividades().mostrar()
        );

        botonTemario.setOnAction(e ->
                new VentanaTemarioUsuario().mostrar()
        );

        botonValoraciones.setOnAction(e ->
                new VentanaValoraciones().mostrar()
        );

        botonBajaMatricula.setOnAction(e ->
                new VentanaBajaMatricula().mostrar()
        );

        botonPerfil.setOnAction(e ->
                new VentanaPerfilUsuario().mostrar()
        );

        botonEliminarCuenta.setOnAction(e ->
                new VentanaEliminarCuenta().mostrar()
        );

        // DISTRIBUCIÓN EN DOS COLUMNAS
        GridPane rejilla = new GridPane();
        rejilla.setHgap(18);
        rejilla.setVgap(15);
        rejilla.setMaxWidth(Double.MAX_VALUE);

        rejilla.add(botonEmpleados, 0, 0);
        rejilla.add(botonTareas, 1, 0);

        rejilla.add(botonCursos, 0, 1);
        rejilla.add(botonAsignaciones, 1, 1);

        rejilla.add(botonRegistrarUsuario, 0, 2);
        rejilla.add(botonUsuarios, 1, 2);

        rejilla.add(botonLogin, 0, 3);
        rejilla.add(botonMatriculas, 1, 3);

        rejilla.add(botonActividades, 0, 4);
        rejilla.add(botonTemario, 1, 4);

        rejilla.add(botonValoraciones, 0, 5);
        rejilla.add(botonBajaMatricula, 1, 5);

        rejilla.add(botonPerfil, 0, 6);
        rejilla.add(botonEliminarCuenta, 1, 6);

        javafx.scene.layout.ColumnConstraints columna1 =
                new javafx.scene.layout.ColumnConstraints();

        columna1.setPercentWidth(50);
        columna1.setHgrow(
                javafx.scene.layout.Priority.ALWAYS
        );

        javafx.scene.layout.ColumnConstraints columna2 =
                new javafx.scene.layout.ColumnConstraints();

        columna2.setPercentWidth(50);
        columna2.setHgrow(
                javafx.scene.layout.Priority.ALWAYS
        );

        rejilla.getColumnConstraints().addAll(
                columna1,
                columna2
        );

        contenido.getChildren().addAll(
                tituloSeccion,
                descripcion,
                rejilla
        );

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + FONDO + ";" +
                "-fx-background-color: " + FONDO + ";" +
                "-fx-border-color: transparent;"
        );

        raiz.setCenter(scroll);

        // ESCENA
        Scene escena = new Scene(raiz, 950, 750);

        escenario.setTitle("Erandio Academy - Panel principal");
        escenario.setScene(escena);
        escenario.setMinWidth(650);
        escenario.setMinHeight(550);

        escenario.show();
    }

    private Button crearBoton(String texto) {

        Button boton = new Button(texto);

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setMinHeight(65);

        boton.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 15;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: " + AZUL + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 15;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-text-fill: " + AZUL_OSCURO + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 15;"
                )
        );

        return boton;
    }
}
