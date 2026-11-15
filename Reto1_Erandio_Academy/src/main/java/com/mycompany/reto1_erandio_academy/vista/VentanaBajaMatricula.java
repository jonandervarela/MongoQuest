
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaBajaMatricula {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";
    private static final String ROJO = "#B3382C";

    private UsuarioDAO usuarioDAO;
    private MatriculaServicio matriculaServicio;

    private ComboBox<Usuario> comboUsuarios;
    private TableView<Matricula> tablaMatriculas;

    private Usuario usuarioActual;
    private boolean esAdministrador;

    // =========================
    // MOSTRAR VENTANA
    // =========================

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No hay ningún usuario conectado."
            );

            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        usuarioDAO = new UsuarioDAO();
        matriculaServicio = new MatriculaServicio();

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

        Label titulo = new Label(
                esAdministrador
                        ? "Gestión de bajas"
                        : "Mis matrículas"
        );

        titulo.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 25)
        );

        titulo.setTextFill(Color.web(TINTA));

        Label subtitulo = new Label(
                esAdministrador
                        ? "Consulta y gestiona las matrículas de los usuarios."
                        : "Consulta tus matrículas y gestiona las bajas de tus cursos."
        );

        subtitulo.setFont(
                Font.font("Segoe UI", 13)
        );

        subtitulo.setTextFill(Color.web(GRIS));
        subtitulo.setWrapText(true);

        VBox textosCabecera = new VBox(
                5,
                titulo,
                subtitulo
        );

        Label etiquetaRol = new Label(
                esAdministrador ? "ADMINISTRACIÓN" : "USUARIO"
        );

        etiquetaRol.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 11)
        );

        etiquetaRol.setTextFill(Color.web(AZUL));

        etiquetaRol.setPadding(
                new Insets(8, 14, 8, 14)
        );

        etiquetaRol.setStyle(
                "-fx-background-color: #DCEEF3;"
                + "-fx-background-radius: 20;"
        );

        Region espacioCabecera = new Region();

        HBox.setHgrow(
                espacioCabecera,
                Priority.ALWAYS
        );

        HBox cabecera = new HBox(
                textosCabecera,
                espacioCabecera,
                etiquetaRol
        );

        cabecera.setAlignment(Pos.CENTER_LEFT);

        cabecera.setPadding(
                new Insets(28, 35, 22, 35)
        );

        // =========================
        // TARJETA DE INFORMACIÓN
        // =========================

        Label tituloInformacion = new Label(
                esAdministrador
                        ? "Gestionar matrículas"
                        : "Gestionar mis matrículas"
        );

        tituloInformacion.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        tituloInformacion.setTextFill(Color.web(TINTA));

        Label descripcionInformacion = new Label(
                esAdministrador
                        ? "Selecciona un usuario para consultar sus matrículas."
                        : "Aquí puedes consultar los cursos en los que estás matriculado."
        );

        descripcionInformacion.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionInformacion.setTextFill(Color.web(GRIS));
        descripcionInformacion.setWrapText(true);

        VBox tarjetaInformacion = new VBox(
                9,
                tituloInformacion,
                descripcionInformacion
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
        // SELECCIONAR USUARIO
        // =========================

        Label etiquetaUsuario = new Label(
                "Seleccionar usuario"
        );

        etiquetaUsuario.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        etiquetaUsuario.setTextFill(Color.web(TINTA));

        comboUsuarios = new ComboBox<>();

        comboUsuarios.setPromptText(
                "Selecciona un usuario..."
        );

        comboUsuarios.setPrefWidth(350);
        comboUsuarios.setMaxWidth(Double.MAX_VALUE);

        comboUsuarios.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        comboUsuarios.setConverter(
                new StringConverter<Usuario>() {

            @Override
            public String toString(Usuario usuario) {

                return usuario == null
                        ? ""
                        : usuario.getNombre();
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });

        comboUsuarios.setCellFactory(lista ->
                new ListCell<Usuario>() {

            @Override
            protected void updateItem(
                    Usuario usuario,
                    boolean vacio
            ) {

                super.updateItem(usuario, vacio);

                setText(
                        vacio || usuario == null
                                ? null
                                : usuario.getNombre()
                );
            }
        });

        HBox filaUsuario = new HBox(
                12,
                etiquetaUsuario,
                comboUsuarios
        );

        filaUsuario.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                comboUsuarios,
                Priority.ALWAYS
        );

        // =========================
        // TABLA DE MATRÍCULAS
        // =========================

        Label tituloTabla = new Label(
                "Listado de matrículas"
        );

        tituloTabla.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 18)
        );

        tituloTabla.setTextFill(Color.web(TINTA));

        Label descripcionTabla = new Label(
                "Selecciona una matrícula activa para poder darte de baja."
        );

        descripcionTabla.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionTabla.setTextFill(Color.web(GRIS));

        tablaMatriculas = new TableView<>();

        tablaMatriculas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tablaMatriculas.setPlaceholder(
                new Label("No hay matrículas para mostrar.")
        );

        tablaMatriculas.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        // COLUMNA CURSO

        TableColumn<Matricula, String> columnaCurso =
                new TableColumn<>("Curso ID");

        columnaCurso.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getCursoId() == null
                                ? ""
                                : dato.getValue().getCursoId().toString()
                )
        );

        columnaCurso.setMinWidth(180);

        // COLUMNA FECHA

        TableColumn<Matricula, String> columnaFecha =
                new TableColumn<>("Fecha");

        columnaFecha.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getFecha()
                )
        );

        columnaFecha.setMinWidth(130);

        // COLUMNA ESTADO

        TableColumn<Matricula, String> columnaEstado =
                new TableColumn<>("Estado");

        columnaEstado.setCellValueFactory(dato ->
                new javafx.beans.property.SimpleStringProperty(
                        dato.getValue().getEstado()
                )
        );

        columnaEstado.setMinWidth(130);

        tablaMatriculas.getColumns().addAll(
                columnaCurso,
                columnaFecha,
                columnaEstado
        );

        // =========================
        // BOTONES
        // =========================

        Button botonBaja = crearBotonRojo(
                "Darse de baja"
        );

        Button botonActualizar = crearBotonSecundario(
                "Actualizar lista"
        );

        botonBaja.setOnAction(
                e -> darDeBaja()
        );

        botonActualizar.setOnAction(
                e -> cargarMatriculas()
        );

        HBox filaBotones = new HBox(
                12,
                botonActualizar,
                botonBaja
        );

        filaBotones.setAlignment(
                Pos.CENTER_RIGHT
        );

        // =========================
        // TARJETA TABLA
        // =========================

        VBox tarjetaTabla = new VBox(
                14,
                tituloTabla,
                descripcionTabla,
                tablaMatriculas,
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

        tablaMatriculas.setPrefHeight(350);
        tablaMatriculas.setMinHeight(220);

        // =========================
        // CONTENIDO SEGÚN EL ROL
        // =========================

        VBox contenido = new VBox(
                20
        );

        contenido.setPadding(
                new Insets(10, 35, 30, 35)
        );

        contenido.getChildren().add(
                tarjetaInformacion
        );

        if (esAdministrador) {

            VBox tarjetaUsuario = new VBox(
                    12,
                    filaUsuario
            );

            tarjetaUsuario.setPadding(
                    new Insets(22)
            );

            tarjetaUsuario.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-background-radius: 12;"
                    + "-fx-border-color: #E1E8ED;"
                    + "-fx-border-radius: 12;"
            );

            contenido.getChildren().add(
                    tarjetaUsuario
            );

        }

        contenido.getChildren().add(
                tarjetaTabla
        );

        // =========================
        // CARGAR DATOS SEGÚN ROL
        // =========================

        if (esAdministrador) {

            cargarUsuarios();

            comboUsuarios.setOnAction(
                    e -> cargarMatriculas()
            );

        } else {

            cargarMatriculas();
        }

        // =========================
        // CONTENIDO CENTRAL
        // =========================

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
        // MOSTRAR VENTANA
        // =========================

        Scene escena = new Scene(
                raiz,
                1050,
                700
        );

        ventana.setTitle(
                esAdministrador
                        ? "Erandio Academy - Gestión de bajas"
                        : "Erandio Academy - Mis matrículas"
        );

        ventana.setMinWidth(800);
        ventana.setMinHeight(550);

        ventana.setScene(escena);

        ventana.setOnHidden(e -> {
            matriculaServicio.cerrar();
            usuarioDAO.cerrar();
        });

        ventana.show();
    }

    // =========================
    // CARGAR USUARIOS
    // =========================

    private void cargarUsuarios() {

        if (!esAdministrador) {
            return;
        }

        List<Usuario> usuarios = usuarioDAO.listar();

        comboUsuarios.setItems(
                FXCollections.observableArrayList(usuarios)
        );
    }

    // =========================
    // CARGAR MATRÍCULAS
    // =========================

    private void cargarMatriculas() {

        Usuario usuario;

        if (esAdministrador) {

            usuario = comboUsuarios.getValue();

            if (usuario == null) {

                tablaMatriculas.getItems().clear();

                return;
            }

        } else {

            usuario = usuarioActual;
        }

        try {

            List<Matricula> matriculas =
                    matriculaServicio.listarPorUsuario(
                            usuario.getId()
                    );

            tablaMatriculas.setItems(
                    FXCollections.observableArrayList(matriculas)
            );

        } catch (Exception ex) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    ex.getMessage()
            );
        }
    }

    // =========================
    // DAR DE BAJA MATRÍCULA
    // =========================

    private void darDeBaja() {

        Matricula matricula = tablaMatriculas
                .getSelectionModel()
                .getSelectedItem();

        if (matricula == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Selecciona una matrícula."
            );

            return;
        }

        // COMPROBAR PERMISOS

        if (!esAdministrador
                && !matricula.getUsuarioId().equals(
                        usuarioActual.getId()
                )) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No tienes permiso para modificar esta matrícula."
            );

            return;
        }

        // COMPROBAR ESTADO

        if (!"ACTIVA".equals(matricula.getEstado())) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Esta matrícula ya no está activa."
            );

            return;
        }

        // CONFIRMAR OPERACIÓN

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle(
                "Confirmar baja"
        );

        confirmacion.setHeaderText(
                "¿Quieres darte de baja de este curso?"
        );

        confirmacion.setContentText(
                "Esta operación eliminará la matrícula seleccionada."
        );

        if (confirmacion.showAndWait().orElse(
                ButtonType.CANCEL
        ) == ButtonType.OK) {

            try {

                boolean resultado =
                        matriculaServicio.eliminar(
                                matricula.getId()
                        );

                if (resultado) {

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Matrícula eliminada correctamente."
                    );

                    cargarMatriculas();

                } else {

                    mostrarMensaje(
                            Alert.AlertType.ERROR,
                            "No se ha podido eliminar la matrícula."
                    );
                }

            } catch (Exception ex) {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        ex.getMessage()
                );
            }
        }
    }

    // =========================
    // BOTÓN ROJO
    // =========================

    private Button crearBotonRojo(String texto) {

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
                "-fx-background-color: " + ROJO + ";"
                + "-fx-background-radius: 7;"
        );

        boton.setOnMouseEntered(e ->
                boton.setStyle(
                        "-fx-background-color: #922C24;"
                        + "-fx-background-radius: 7;"
                )
        );

        boton.setOnMouseExited(e ->
                boton.setStyle(
                        "-fx-background-color: " + ROJO + ";"
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

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
