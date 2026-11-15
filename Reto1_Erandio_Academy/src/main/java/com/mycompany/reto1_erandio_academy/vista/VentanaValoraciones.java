
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Valoracion;
import com.mycompany.reto1_erandio_academy.servicio.ValoracionServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

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

import java.util.HashMap;
import java.util.Map;

public class VentanaValoraciones {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

    private CursoDAO cursoDAO;
    private UsuarioDAO usuarioDAO;
    private ValoracionServicio valoracionServicio;

    private ComboBox<Curso> comboCursos;
    private ComboBox<Usuario> comboUsuarios;
    private ComboBox<Integer> comboPuntuacion;

    private TextArea campoComentario;

    private TableView<Valoracion> tabla;

    private Usuario usuarioActual;
    private boolean esAdministrador;

    private Map<String, String> nombresUsuarios = new HashMap<>();
    private Map<String, String> nombresCursos = new HashMap<>();

    // =========================
    // REGISTRAR LOG
    // =========================

    private void registrarLog(
            String operacion,
            String resultado,
            String error
    ) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "valoraciones",
                    resultado,
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    // =========================
    // MOSTRAR VENTANA
    // =========================

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado."
            );
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        cursoDAO = new CursoDAO();
        usuarioDAO = new UsuarioDAO();
        valoracionServicio = new ValoracionServicio();

        Stage ventana = new Stage();

        // =========================
        // PANEL PRINCIPAL
        // =========================

        VBox raiz = new VBox();
        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // CABECERA
        // =========================

        Label titulo = new Label("Valoraciones");

        titulo.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 25)
        );

        titulo.setTextFill(Color.web(TINTA));

        Label subtitulo = new Label(
                "Comparte tu opinión y consulta las valoraciones de los cursos."
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
        HBox.setHgrow(espacioCabecera, Priority.ALWAYS);

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
        // TARJETA DE FORMULARIO
        // =========================

        Label tituloFormulario = new Label(
                "Registrar valoración"
        );

        tituloFormulario.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        tituloFormulario.setTextFill(Color.web(TINTA));

        Label descripcionFormulario = new Label(
                "Selecciona un curso, indica tu puntuación y escribe un comentario."
        );

        descripcionFormulario.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionFormulario.setTextFill(Color.web(GRIS));
        descripcionFormulario.setWrapText(true);

        VBox tarjetaFormulario = new VBox(15);

        tarjetaFormulario.setPadding(
                new Insets(23)
        );

        tarjetaFormulario.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        tarjetaFormulario.getChildren().addAll(
                tituloFormulario,
                descripcionFormulario
        );

        // =========================
        // SELECCIONAR USUARIO
        // =========================

        if (esAdministrador) {

            Label etiquetaUsuario = crearEtiqueta(
                    "Seleccionar usuario"
            );

            comboUsuarios = new ComboBox<>();

            comboUsuarios.setPromptText(
                    "Selecciona un usuario..."
            );

            comboUsuarios.setMaxWidth(Double.MAX_VALUE);

            comboUsuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioDAO.listar()
                    )
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

            tarjetaFormulario.getChildren().addAll(
                    etiquetaUsuario,
                    comboUsuarios
            );

        } else {

            Label etiquetaUsuario = crearEtiqueta(
                    "Usuario"
            );

            Label nombreUsuario = new Label(
                    usuarioActual.getNombre()
            );

            nombreUsuario.setFont(
                    Font.font("Segoe UI", FontWeight.BOLD, 14)
            );

            nombreUsuario.setTextFill(Color.web(TINTA));

            nombreUsuario.setPadding(
                    new Insets(12)
            );

            nombreUsuario.setMaxWidth(Double.MAX_VALUE);

            nombreUsuario.setStyle(
                    "-fx-background-color: #F2F5F7;"
                    + "-fx-background-radius: 7;"
            );

            tarjetaFormulario.getChildren().addAll(
                    etiquetaUsuario,
                    nombreUsuario
            );
        }

        // =========================
        // SELECCIONAR CURSO
        // =========================

        Label etiquetaCurso = crearEtiqueta(
                "Seleccionar curso"
        );

        comboCursos = new ComboBox<>();

        comboCursos.setPromptText(
                "Selecciona un curso..."
        );

        comboCursos.setMaxWidth(Double.MAX_VALUE);

        comboCursos.setItems(
                FXCollections.observableArrayList(
                        cursoDAO.listar()
                )
        );

        comboCursos.setConverter(
                new StringConverter<Curso>() {

            @Override
            public String toString(Curso curso) {
                return curso == null
                        ? ""
                        : curso.getNombre();
            }

            @Override
            public Curso fromString(String texto) {
                return null;
            }
        });

        comboCursos.setCellFactory(lista ->
                new ListCell<Curso>() {

            @Override
            protected void updateItem(
                    Curso curso,
                    boolean vacio
            ) {
                super.updateItem(curso, vacio);

                setText(
                        vacio || curso == null
                                ? null
                                : curso.getNombre()
                );
            }
        });

        // =========================
        // PUNTUACIÓN
        // =========================

        Label etiquetaPuntuacion = crearEtiqueta(
                "Puntuación (1 a 5)"
        );

        comboPuntuacion = new ComboBox<>();

        comboPuntuacion.setItems(
                FXCollections.observableArrayList(
                        1, 2, 3, 4, 5
                )
        );

        comboPuntuacion.setPromptText(
                "Selecciona una puntuación"
        );

        comboPuntuacion.setMaxWidth(Double.MAX_VALUE);

        // =========================
        // COMENTARIO
        // =========================

        Label etiquetaComentario = crearEtiqueta(
                "Comentario"
        );

        campoComentario = new TextArea();

        campoComentario.setPromptText(
                "Escribe tu opinión sobre el curso..."
        );

        campoComentario.setPrefRowCount(4);
        campoComentario.setWrapText(true);
        campoComentario.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 13px;"
        );

        // =========================
        // BOTÓN GUARDAR
        // =========================

        Button botonGuardar = crearBoton(
                "Guardar valoración"
        );

        botonGuardar.setOnAction(
                e -> guardarValoracion()
        );

        HBox filaBoton = new HBox(
                botonGuardar
        );

        filaBoton.setAlignment(
                Pos.CENTER_RIGHT
        );

        tarjetaFormulario.getChildren().addAll(
                etiquetaCurso,
                comboCursos,
                etiquetaPuntuacion,
                comboPuntuacion,
                etiquetaComentario,
                campoComentario,
                filaBoton
        );

        // =========================
        // TABLA DE VALORACIONES
        // =========================

        Label tituloTabla = new Label(
                "Valoraciones registradas"
        );

        tituloTabla.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        tituloTabla.setTextFill(Color.web(TINTA));

        Label descripcionTabla = new Label(
                esAdministrador
                        ? "Consulta todas las valoraciones registradas."
                        : "Consulta las valoraciones que has realizado."
        );

        descripcionTabla.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionTabla.setTextFill(Color.web(GRIS));

        tabla = new TableView<>();

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        tabla.setPlaceholder(
                new Label("Todavía no hay valoraciones registradas.")
        );

        tabla.setStyle(
                "-fx-font-family: 'Segoe UI';"
                + "-fx-font-size: 12px;"
        );

        crearColumnas();

        // =========================
        // BOTÓN ACTUALIZAR
        // =========================

        Button botonActualizar = crearBoton(
                "Actualizar valoraciones"
        );

        botonActualizar.setOnAction(e -> {
            cargarNombres();
            cargarValoraciones();
            tabla.refresh();
        });

        HBox filaActualizar = new HBox(
                botonActualizar
        );

        filaActualizar.setAlignment(
                Pos.CENTER_RIGHT
        );

        // =========================
        // TARJETA TABLA
        // =========================

        VBox tarjetaTabla = new VBox(
                14,
                tituloTabla,
                descripcionTabla,
                tabla,
                filaActualizar
        );

        tarjetaTabla.setPadding(
                new Insets(23)
        );

        tarjetaTabla.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        tabla.setPrefHeight(280);
        tabla.setMinHeight(200);

        // =========================
        // CONTENIDO CENTRAL
        // =========================

        VBox contenido = new VBox(
                20,
                tarjetaFormulario,
                tarjetaTabla
        );

        contenido.setPadding(
                new Insets(10, 35, 30, 35)
        );

        VBox raizContenido = new VBox(
                cabecera,
                contenido
        );

        raizContenido.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // SCROLL
        // =========================

        ScrollPane scroll = new ScrollPane();

        scroll.setContent(raizContenido);
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

        raiz.getChildren().add(scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // =========================
        // CARGAR DATOS
        // =========================

        cargarNombres();
        cargarValoraciones();

        // =========================
        // MOSTRAR VENTANA
        // =========================

        Scene escena = new Scene(
                raiz,
                1050,
                750
        );

        ventana.setTitle(
                "Erandio Academy - Valoraciones"
        );

        ventana.setMinWidth(800);
        ventana.setMinHeight(600);
        ventana.setScene(escena);

        ventana.setOnHidden(e -> {
            valoracionServicio.cerrar();
            usuarioDAO.cerrar();
            cursoDAO.cerrar();
        });

        ventana.show();
    }

    // =========================
    // CREAR ETIQUETAS
    // =========================

    private Label crearEtiqueta(String texto) {

        Label etiqueta = new Label(texto);

        etiqueta.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        etiqueta.setTextFill(Color.web(TINTA));

        return etiqueta;
    }

    // =========================
    // CREAR BOTONES
    // =========================

    private Button crearBoton(String texto) {

        Button boton = new Button(texto);

        boton.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        boton.setTextFill(Color.WHITE);
        boton.setPadding(
                new Insets(11, 20, 11, 20)
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
    // CARGAR NOMBRES
    // =========================

    private void cargarNombres() {

        nombresUsuarios.clear();
        nombresCursos.clear();

        for (Usuario usuario : usuarioDAO.listar()) {

            nombresUsuarios.put(
                    usuario.getId().toString(),
                    usuario.getNombre()
            );
        }

        for (Curso curso : cursoDAO.listar()) {

            nombresCursos.put(
                    curso.getId().toString(),
                    curso.getNombre()
            );
        }
    }

    // =========================
    // CREAR COLUMNAS
    // =========================

    private void crearColumnas() {

        TableColumn<Valoracion, String> usuarioColumna =
                new TableColumn<>("Usuario");

        usuarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresUsuarios.getOrDefault(
                                d.getValue().getUsuarioId().toString(),
                                "Usuario desconocido"
                        )
                )
        );

        TableColumn<Valoracion, String> cursoColumna =
                new TableColumn<>("Curso");

        cursoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(
                                d.getValue().getCursoId().toString(),
                                "Curso desconocido"
                        )
                )
        );

        TableColumn<Valoracion, String> puntuacionColumna =
                new TableColumn<>("Puntuación");

        puntuacionColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        String.valueOf(
                                d.getValue().getPuntuacion()
                        )
                )
        );

        TableColumn<Valoracion, String> comentarioColumna =
                new TableColumn<>("Comentario");

        comentarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getComentario()
                )
        );

        tabla.getColumns().addAll(
                usuarioColumna,
                cursoColumna,
                puntuacionColumna,
                comentarioColumna
        );
    }

    // =========================
    // GUARDAR VALORACIÓN
    // =========================

    private void guardarValoracion() {

        Usuario usuario;

        if (esAdministrador) {
            usuario = comboUsuarios.getValue();
        } else {
            usuario = usuarioActual;
        }

        Curso curso = comboCursos.getValue();

        Integer puntuacion = comboPuntuacion.getValue();

        String comentario = campoComentario.getText().trim();

        if (usuario == null || curso == null || puntuacion == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Debes seleccionar usuario, curso y puntuación."
            );

            return;
        }

        try {

            Valoracion valoracion = new Valoracion(
                    usuario.getId(),
                    curso.getId(),
                    puntuacion,
                    comentario
            );

            boolean resultado =
                    valoracionServicio.registrar(valoracion);

            if (resultado) {

                registrarLog(
                        "INSERT",
                        "OK",
                        null
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Valoración guardada correctamente."
                );

                campoComentario.clear();
                comboPuntuacion.setValue(null);

                cargarValoraciones();

            } else {

                registrarLog(
                        "INSERT",
                        "ERROR",
                        "No se ha podido guardar la valoración."
                );

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se ha podido guardar la valoración."
                );
            }

        } catch (Exception ex) {

            registrarLog(
                    "INSERT",
                    "ERROR",
                    ex.getMessage()
            );

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    ex.getMessage()
            );
        }
    }

    // =========================
    // CARGAR VALORACIONES
    // =========================

    private void cargarValoraciones() {

        if (esAdministrador) {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            valoracionServicio.listar()
                    )
            );

        } else {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            valoracionServicio.listarPorUsuario(
                                    usuarioActual.getId()
                            )
                    )
            );
        }
    }

    // =========================
    // MOSTRAR MENSAJES
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
