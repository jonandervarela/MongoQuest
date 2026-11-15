
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaCursosDisponibles {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    private final CursoServicio cursoServicio = new CursoServicio();
    private final MatriculaServicio matriculaServicio = new MatriculaServicio();

    private TableView<Curso> tabla;

    private Usuario usuarioActual;

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarError("No hay ningún usuario conectado.");
            cerrarServicios();
            return;
        }

        Stage ventana = new Stage();

        // =========================
        // PANEL PRINCIPAL
        // =========================

        BorderPane raiz = new BorderPane();
        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // CABECERA
        // =========================

        Label titulo = new Label("Cursos disponibles");

        titulo.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 25)
        );

        titulo.setTextFill(Color.web(TINTA));

        Label subtitulo = new Label(
                "Descubre los cursos disponibles y matricúlate."
        );

        subtitulo.setFont(
                Font.font("Segoe UI", 13)
        );

        subtitulo.setTextFill(Color.web(GRIS));

        VBox textosCabecera = new VBox(
                5,
                titulo,
                subtitulo
        );

        Label etiqueta = new Label("FORMACIÓN");

        etiqueta.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 11)
        );

        etiqueta.setTextFill(Color.web(AZUL));

        etiqueta.setPadding(
                new Insets(8, 14, 8, 14)
        );

        etiqueta.setStyle(
                "-fx-background-color: #DCEEF3;"
                + "-fx-background-radius: 20;"
        );

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox cabecera = new HBox(
                textosCabecera,
                espacio,
                etiqueta
        );

        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(
                new Insets(28, 35, 22, 35)
        );

        // =========================
        // TARJETA DE INFORMACIÓN
        // =========================

        Label tituloTarjeta = new Label(
                "Explora nuestros cursos"
        );

        tituloTarjeta.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        tituloTarjeta.setTextFill(Color.web(TINTA));

        Label descripcionTarjeta = new Label(
                "Selecciona un curso de la lista para ver sus detalles y realizar tu matrícula."
        );

        descripcionTarjeta.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionTarjeta.setTextFill(Color.web(GRIS));
        descripcionTarjeta.setWrapText(true);

        VBox tarjetaInformacion = new VBox(
                9,
                tituloTarjeta,
                descripcionTarjeta
        );

        tarjetaInformacion.setPadding(
                new Insets(22)
        );

        tarjetaInformacion.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        // =========================
        // TABLA DE CURSOS
        // =========================

        Label tituloTabla = new Label(
                "Listado de cursos"
        );

        tituloTabla.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 18)
        );

        tituloTabla.setTextFill(Color.web(TINTA));

        tabla = new TableView<>();

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("No hay cursos disponibles actualmente.")
        );

        tabla.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        TableColumn<Curso, String> columnaNombre =
                new TableColumn<>("Nombre");

        columnaNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        columnaNombre.setMinWidth(150);
        columnaNombre.setPrefWidth(220);

        TableColumn<Curso, String> columnaDescripcion =
                new TableColumn<>("Descripción");

        columnaDescripcion.setCellValueFactory(
                new PropertyValueFactory<>("descripcion")
        );

        columnaDescripcion.setMinWidth(250);
        columnaDescripcion.setPrefWidth(450);

        TableColumn<Curso, String> columnaDuracion =
                new TableColumn<>("Duración");

        columnaDuracion.setCellValueFactory(
                new PropertyValueFactory<>("duracion")
        );

        columnaDuracion.setMinWidth(100);
        columnaDuracion.setPrefWidth(130);

        tabla.getColumns().addAll(
                columnaNombre,
                columnaDescripcion,
                columnaDuracion
        );

        // =========================
        // BOTONES
        // =========================

        Button botonMatricular = crearBoton(
                "Matricularme"
        );

        Button botonActualizar = crearBotonSecundario(
                "Actualizar cursos"
        );

        botonMatricular.setOnAction(
                e -> matricularUsuario()
        );

        botonActualizar.setOnAction(
                e -> cargarCursos()
        );

        HBox filaBotones = new HBox(
                12,
                botonActualizar,
                botonMatricular
        );

        filaBotones.setAlignment(
                Pos.CENTER_RIGHT
        );

        // =========================
        // TARJETA TABLA
        // =========================

        VBox tarjetaTabla = new VBox(
                15,
                tituloTabla,
                tabla,
                filaBotones
        );

        tarjetaTabla.setPadding(
                new Insets(22)
        );

        tarjetaTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        tabla.setPrefHeight(350);
        tabla.setMinHeight(230);

        // =========================
        // CONTENIDO CENTRAL
        // =========================

        VBox contenido = new VBox(
                20,
                tarjetaInformacion,
                tarjetaTabla
        );

        contenido.setPadding(
                new Insets(10, 35, 30, 35)
        );

        VBox zonaCentral = new VBox(
                cabecera,
                contenido
        );

        zonaCentral.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // SCROLL
        // =========================

        ScrollPane scroll = new ScrollPane();

        scroll.setContent(zonaCentral);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(false);
        scroll.setPannable(true);

        scroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scroll.setStyle(
                "-fx-background: " + FONDO + ";"
                + "-fx-background-color: " + FONDO + ";"
                + "-fx-border-color: transparent;"
        );

        raiz.setCenter(scroll);

        // =========================
        // CARGAR CURSOS
        // =========================

        cargarCursos();

        // =========================
        // MOSTRAR VENTANA
        // =========================

        Scene escena = new Scene(
                raiz,
                1050,
                700
        );

        ventana.setTitle(
                "Erandio Academy - Cursos disponibles"
        );

        ventana.setMinWidth(800);
        ventana.setMinHeight(550);
        ventana.setScene(escena);

        ventana.setOnHidden(e -> cerrarServicios());

        ventana.show();
    }

    // =========================
    // CARGAR CURSOS ACTIVOS
    // =========================

    private void cargarCursos() {

        try {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            cursoServicio.listarActivos()
                    )
            );

        } catch (Exception ex) {

            mostrarError(ex.getMessage());
        }
    }

    // =========================
    // MATRICULAR USUARIO
    // =========================

    private void matricularUsuario() {

        Curso seleccionado =
                tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAviso(
                    "Selecciona un curso."
            );

            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle(
                "Confirmar matrícula"
        );

        confirmacion.setHeaderText(
                "¿Quieres matricularte?"
        );

        confirmacion.setContentText(
                "Curso: " + seleccionado.getNombre()
        );

        if (confirmacion.showAndWait().orElse(
                ButtonType.CANCEL
        ) == ButtonType.OK) {

            try {

                matriculaServicio.matricular(
                        usuarioActual.getId(),
                        seleccionado.getId()
                );

                mostrarInformacion(
                        "Te has matriculado correctamente."
                );

            } catch (Exception ex) {

                mostrarError(ex.getMessage());
            }
        }
    }

    // =========================
    // BOTÓN PRINCIPAL
    // =========================

    private Button crearBoton(String texto) {

        Button boton = new Button(texto);

        boton.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        boton.setTextFill(Color.WHITE);

        boton.setPadding(
                new Insets(11, 22, 11, 22)
        );

        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 7;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: #18566D;"
                        + "-fx-background-radius: 7;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: " + AZUL + ";"
                        + "-fx-background-radius: 7;"
                )
        );

        return boton;
    }

    // =========================
    // BOTÓN SECUNDARIO
    // =========================

    private Button crearBotonSecundario(String texto) {

        Button boton = new Button(texto);

        boton.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        boton.setTextFill(Color.web(TINTA));

        boton.setPadding(
                new Insets(11, 20, 11, 20)
        );

        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: #E5EBEF;"
                + "-fx-background-radius: 7;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: #D5E0E6;"
                        + "-fx-background-radius: 7;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: #E5EBEF;"
                        + "-fx-background-radius: 7;"
                )
        );

        return boton;
    }

    // =========================
    // MENSAJES
    // =========================

    private void mostrarAviso(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.WARNING
        );

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION
        );

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(
                Alert.AlertType.ERROR
        );

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // =========================
    // CERRAR SERVICIOS
    // =========================

    private void cerrarServicios() {

        cursoServicio.cerrar();
        matriculaServicio.cerrar();
    }
}
