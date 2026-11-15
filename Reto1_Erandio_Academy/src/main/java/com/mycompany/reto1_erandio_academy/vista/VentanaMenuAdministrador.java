
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaMenuAdministrador {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String BLANCO = "#FFFFFF";
    private static final String GRIS = "#64748B";

    public void mostrar() {

        Stage ventana = new Stage();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        // PANEL LATERAL
        VBox lateral = new VBox(18);
        lateral.setPadding(new Insets(28, 18, 25, 18));
        lateral.setPrefWidth(235);
        lateral.setStyle("-fx-background-color: " + TINTA + ";");

        Label logo = new Label("ERANDIO");
        logo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 25));
        logo.setTextFill(Color.WHITE);

        Label subtituloLogo = new Label("ACADEMY");
        subtituloLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        subtituloLogo.setTextFill(Color.web("#65B6D0"));

        VBox marca = new VBox(2, logo, subtituloLogo);

        Label etiquetaMenu = new Label("MENÚ PRINCIPAL");
        etiquetaMenu.setTextFill(Color.web("#9FB3C4"));
        etiquetaMenu.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));

        Label administrador = new Label("Administrador");
        administrador.setTextFill(Color.WHITE);
        administrador.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label nombreUsuario = new Label(
                usuario == null ? "" : usuario.getNombre()
        );
        nombreUsuario.setTextFill(Color.web("#B9CAD6"));
        nombreUsuario.setFont(Font.font("Segoe UI", 12));

        VBox datosUsuario = new VBox(5, administrador, nombreUsuario);

        // BOTONES LATERALES
        Button botonEmpleados = crearBoton("Empleados");
        Button botonTareas = crearBoton("Tareas");
        Button botonCursos = crearBoton("Cursos");
        Button botonAsignaciones = crearBoton("Asignaciones");
        Button botonUsuarios = crearBoton("Usuarios");
        Button botonMatriculas = crearBoton("Matrículas");
        Button botonActividades = crearBoton("Actividades");
        Button botonEmpresa = crearBoton("Datos de empresa");

        Button botonSalir = crearBoton("Cerrar sesión");
        botonSalir.setStyle(
                "-fx-background-color: #9E3B3B;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 8;"
                + "-fx-font-weight: bold;"
        );

        // ACCIONES
        botonEmpleados.setOnAction(e ->
                new VentanaListaEmpleados().mostrar());

        botonTareas.setOnAction(e ->
                new VentanaTareas().mostrar());

        botonCursos.setOnAction(e ->
                new VentanaCursos().mostrar());

        botonAsignaciones.setOnAction(e ->
                new VentanaAsignaciones().mostrar());

        botonUsuarios.setOnAction(e ->
                new VentanaListaUsuarios().mostrar());

        botonMatriculas.setOnAction(e ->
                new VentanaMatriculas().mostrar());

        botonActividades.setOnAction(e ->
                new VentanaActividades().mostrar());

        botonEmpresa.setOnAction(e ->
                new VentanaEmpresa().mostrar());

        botonSalir.setOnAction(e -> {
            SesionUsuario.cerrarSesion();
            new VentanaLogin().mostrar();
            ventana.close();
        });

        lateral.getChildren().addAll(
                marca,
                etiquetaMenu,
                botonEmpleados,
                botonTareas,
                botonCursos,
                botonAsignaciones,
                botonUsuarios,
                botonMatriculas,
                botonActividades,
                botonEmpresa
        );

        VBox.setVgrow(botonSalir, Priority.NEVER);

        lateral.getChildren().addAll(
                new javafx.scene.layout.Region(),
                datosUsuario,
                botonSalir
        );

        // CABECERA PRINCIPAL
        Label titulo = new Label("Panel de administración");
        titulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 27));
        titulo.setTextFill(Color.web(TINTA));

        Label descripcion = new Label(
                "Gestiona los recursos y la actividad de tu academia."
        );
        descripcion.setFont(Font.font("Segoe UI", 14));
        descripcion.setTextFill(Color.web(GRIS));

        VBox cabecera = new VBox(7, titulo, descripcion);
        cabecera.setPadding(new Insets(30, 35, 20, 35));

        // TARJETAS DE GESTIÓN
        GridPane tarjetas = new GridPane();
        tarjetas.setHgap(18);
        tarjetas.setVgap(18);
        tarjetas.setPadding(new Insets(15, 35, 35, 35));

        agregarTarjeta(tarjetas, "EMPLEADOS",
                "Gestionar profesores y empleados",
                botonEmpleados, 0, 0);

        agregarTarjeta(tarjetas, "TAREAS",
                "Crear y gestionar tareas",
                botonTareas, 1, 0);

        agregarTarjeta(tarjetas, "CURSOS",
                "Administrar cursos académicos",
                botonCursos, 0, 1);

        agregarTarjeta(tarjetas, "ASIGNACIONES",
                "Asignar profesores a cursos",
                botonAsignaciones, 1, 1);

        agregarTarjeta(tarjetas, "USUARIOS",
                "Gestionar cuentas de alumnos",
                botonUsuarios, 0, 2);

        agregarTarjeta(tarjetas, "MATRÍCULAS",
                "Consultar y gestionar matrículas",
                botonMatriculas, 1, 2);

        agregarTarjeta(tarjetas, "ACTIVIDADES",
                "Gestionar actividades académicas",
                botonActividades, 0, 3);

        agregarTarjeta(tarjetas, "EMPRESA",
                "Configurar datos de la empresa",
                botonEmpresa, 1, 3);

        BorderPane raiz = new BorderPane();
        raiz.setLeft(lateral);
        raiz.setCenter(new VBox(cabecera, tarjetas));
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        ventana.setTitle("Erandio Academy - Administración");
        ventana.setMinWidth(850);
        ventana.setMinHeight(600);
        ventana.setWidth(1050);
        ventana.setHeight(760);

        Scene escena = new Scene(raiz);
        ventana.setScene(escena);
        ventana.centerOnScreen();
        ventana.show();
    }

    // CREAR BOTÓN LATERAL
    private Button crearBoton(String texto) {

        Button boton = new Button(texto);

        boton.setMaxWidth(Double.MAX_VALUE);
        boton.setAlignment(Pos.CENTER_LEFT);
        boton.setPadding(new Insets(12, 14, 12, 14));

        boton.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);

        String normal =
                "-fx-background-color: transparent;"
                + "-fx-background-radius: 8;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;";

        String encima =
                "-fx-background-color: #1F6F8B;"
                + "-fx-background-radius: 8;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;";

        boton.setStyle(normal);

        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(normal));

        return boton;
    }

    // CREAR TARJETA
    private void agregarTarjeta(
            GridPane grid,
            String titulo,
            String descripcion,
            Button boton,
            int columna,
            int fila) {

        Label nombre = new Label(titulo);
        nombre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        nombre.setTextFill(Color.web(TINTA));

        Label detalle = new Label(descripcion);
        detalle.setFont(Font.font("Segoe UI", 12));
        detalle.setTextFill(Color.web(GRIS));
        detalle.setWrapText(true);

        Label accion = new Label("Abrir gestión  →");
        accion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        accion.setTextFill(Color.web(AZUL));

        VBox tarjeta = new VBox(12, nombre, detalle, accion);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMinHeight(125);
        tarjeta.setMaxWidth(Double.MAX_VALUE);
        tarjeta.setStyle(
                "-fx-background-color: " + BLANCO + ";"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E0E7ED;"
                + "-fx-border-radius: 12;"
        );

        tarjeta.setCursor(Cursor.HAND);
        tarjeta.setOnMouseClicked(e -> boton.fire());

        tarjeta.setOnMouseEntered(e ->
                tarjeta.setStyle(
                        "-fx-background-color: #F8FCFE;"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: " + AZUL + ";"
                        + "-fx-border-radius: 12;"
                )
        );

        tarjeta.setOnMouseExited(e ->
                tarjeta.setStyle(
                        "-fx-background-color: " + BLANCO + ";"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #E0E7ED;"
                        + "-fx-border-radius: 12;"
                )
        );

        grid.add(tarjeta, columna, fila);

        GridPane.setHgrow(tarjeta, Priority.ALWAYS);
    }
}
