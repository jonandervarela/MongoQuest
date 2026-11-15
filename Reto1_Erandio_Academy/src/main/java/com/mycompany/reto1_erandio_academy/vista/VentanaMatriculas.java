
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Matricula;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VentanaMatriculas {

    private UsuarioDAO usuarioDAO;
    private CursoDAO cursoDAO;
    private MatriculaServicio servicio;

    private final TableView<Matricula> tabla = new TableView<>();

    private Usuario usuarioActual;
    private boolean esAdministrador;

    private final Map<String, String> nombresUsuarios =
            new HashMap<>();

    private final Map<String, String> nombresCursos =
            new HashMap<>();

    private final String AZUL_OSCURO = "#12283A";
    private final String AZUL = "#1F6F8B";
    private final String FONDO = "#F2F5F7";
    private final String GRIS = "#64748B";

    // ==========================================
    // REGISTRAR LOG
    // ==========================================

    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "matriculas",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    // ==========================================
    // MOSTRAR VENTANA
    // ==========================================

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado.");
            return;
        }

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        usuarioDAO = new UsuarioDAO();
        cursoDAO = new CursoDAO();
        servicio = new MatriculaServicio();

        Stage ventana = new Stage();

        ventana.setTitle(
                "Erandio Academy - Gestión de matrículas");

        // ==========================================
        // LISTA Y FILTRO
        // ==========================================

        ObservableList<Matricula> datosMatriculas =
                FXCollections.observableArrayList();

        FilteredList<Matricula> matriculasFiltradas =
                new FilteredList<>(datosMatriculas, m -> true);

        tabla.setItems(matriculasFiltradas);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.setPlaceholder(
                new Label("No hay matrículas registradas."));

        // ==========================================
        // CABECERA
        // ==========================================

        Label etiqueta = new Label("ADMINISTRACIÓN ACADÉMICA");

        etiqueta.setStyle(
                "-fx-text-fill: " + AZUL + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label titulo = new Label("Gestión de matrículas");

        titulo.setStyle(
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;");

        Label descripcion = new Label(
                esAdministrador
                ? "Administra las inscripciones de los usuarios en los cursos."
                : "Consulta las matrículas de tus cursos.");

        descripcion.setStyle(
                "-fx-text-fill: " + GRIS + ";" +
                "-fx-font-size: 13px;");

        VBox cabecera = new VBox(
                6,
                etiqueta,
                titulo,
                descripcion);

        // ==========================================
        // PANEL DE NUEVA MATRÍCULA - SOLO ADMIN
        // ==========================================

        VBox panelMatricula = new VBox(14);

        if (esAdministrador) {

            Label tituloNueva = new Label("Nueva matrícula");

            tituloNueva.setStyle(
                    "-fx-font-size: 17px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: " + AZUL_OSCURO + ";");

            ComboBox<Usuario> usuarios = new ComboBox<>();

            usuarios.setPromptText("Selecciona un usuario");
            usuarios.setMaxWidth(Double.MAX_VALUE);
            usuarios.setPrefHeight(40);

            ComboBox<Curso> cursos = new ComboBox<>();

            cursos.setPromptText("Selecciona un curso");
            cursos.setMaxWidth(Double.MAX_VALUE);
            cursos.setPrefHeight(40);

            // CARGAR USUARIOS
            usuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioDAO.listar()));

            // MOSTRAR NOMBRE DEL USUARIO
            usuarios.setCellFactory(lista ->
                    new ListCell<Usuario>() {

                @Override
                protected void updateItem(
                        Usuario usuario,
                        boolean vacio) {

                    super.updateItem(usuario, vacio);

                    setText(vacio || usuario == null
                            ? null
                            : usuario.getNombre());
                }
            });

            usuarios.setButtonCell(new ListCell<Usuario>() {

                @Override
                protected void updateItem(
                        Usuario usuario,
                        boolean vacio) {

                    super.updateItem(usuario, vacio);

                    setText(vacio || usuario == null
                            ? null
                            : usuario.getNombre());
                }
            });

            // CARGAR CURSOS
            cursos.setItems(
                    FXCollections.observableArrayList(
                            cursoDAO.listar()));

            // MOSTRAR NOMBRE DEL CURSO
            cursos.setCellFactory(lista ->
                    new ListCell<Curso>() {

                @Override
                protected void updateItem(
                        Curso curso,
                        boolean vacio) {

                    super.updateItem(curso, vacio);

                    setText(vacio || curso == null
                            ? null
                            : curso.getNombre());
                }
            });

            cursos.setButtonCell(new ListCell<Curso>() {

                @Override
                protected void updateItem(
                        Curso curso,
                        boolean vacio) {

                    super.updateItem(curso, vacio);

                    setText(vacio || curso == null
                            ? null
                            : curso.getNombre());
                }
            });

            Button matricular = crearBoton(
                    "Matricular usuario", AZUL);

            matricular.setOnAction(e -> {

                Usuario usuario = usuarios.getValue();
                Curso curso = cursos.getValue();

                if (usuario == null || curso == null) {

                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Selecciona un usuario y un curso.");

                    return;
                }

                try {

                    servicio.matricular(
                            usuario.getId(),
                            curso.getId());

                    registrarLog("INSERT", "OK", null);

                    cargarNombres();

                    crearColumnas();

                    cargarMatriculas(
                            datosMatriculas);

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Usuario matriculado correctamente.");

                    usuarios.getSelectionModel().clearSelection();
                    cursos.getSelectionModel().clearSelection();

                } catch (Exception ex) {

                    registrarLog(
                            "INSERT",
                            "ERROR",
                            ex.getMessage());

                    mostrarMensaje(
                            Alert.AlertType.ERROR,
                            ex.getMessage());
                }
            });

            panelMatricula.getChildren().addAll(
                    tituloNueva,
                    new Label("Seleccionar usuario"),
                    usuarios,
                    new Label("Seleccionar curso"),
                    cursos,
                    matricular);

            panelMatricula.setPadding(new Insets(20));

            panelMatricula.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 14;" +
                    "-fx-border-color: #E1E7EC;" +
                    "-fx-border-radius: 14;");
        }

        // ==========================================
        // BUSCADOR
        // ==========================================

        TextField buscador = new TextField();

        buscador.setPromptText(
                "Buscar por usuario, curso, fecha o estado...");

        buscador.setPrefHeight(40);

        buscador.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #DCE3E8;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;");

        buscador.textProperty().addListener(
                (obs, anterior, nuevo) -> {

            String filtro = nuevo == null
                    ? ""
                    : nuevo.toLowerCase().trim();

            matriculasFiltradas.setPredicate(matricula -> {

                if (filtro.isEmpty()) {
                    return true;
                }

                String nombreUsuario =
                        nombresUsuarios.getOrDefault(
                                matricula.getUsuarioId().toString(),
                                "Usuario desconocido");

                String nombreCurso =
                        nombresCursos.getOrDefault(
                                matricula.getCursoId().toString(),
                                "Curso desconocido");

                return contiene(nombreUsuario, filtro)
                        || contiene(nombreCurso, filtro)
                        || contiene(matricula.getFecha(), filtro)
                        || contiene(matricula.getEstado(), filtro);
            });
        });

        // ==========================================
        // COLUMNAS
        // ==========================================

        cargarNombres();

        crearColumnas();

        // ==========================================
        // BOTONES
        // ==========================================

        Button actualizar = crearBoton(
                "Actualizar lista", AZUL_OSCURO);

        actualizar.setOnAction(e -> {

            try {

                cargarNombres();

                crearColumnas();

                cargarMatriculas(datosMatriculas);

                tabla.refresh();

            } catch (Exception ex) {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        ex.getMessage());
            }
        });

        Button eliminar = null;

        // ==========================================
        // DAR DE BAJA - SOLO ADMIN
        // ==========================================

        if (esAdministrador) {

            eliminar = crearBoton(
                    "Dar de baja", "#C0392B");

            eliminar.setOnAction(e -> {

                Matricula seleccionada =
                        tabla.getSelectionModel().getSelectedItem();

                if (seleccionada == null) {

                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Selecciona una matrícula.");

                    return;
                }

                Alert confirmacion =
                        new Alert(Alert.AlertType.CONFIRMATION);

                confirmacion.setTitle("Dar de baja");

                confirmacion.setHeaderText(
                        "Confirmar baja de matrícula");

                confirmacion.setContentText(
                        "¿Quieres eliminar esta matrícula?");

                Optional<ButtonType> resultado =
                        confirmacion.showAndWait();

                if (resultado.isPresent()
                        && resultado.get() == ButtonType.OK) {

                    try {

                        servicio.eliminar(
                                seleccionada.getId());

                        registrarLog("DELETE", "OK", null);

                        cargarMatriculas(datosMatriculas);

                        mostrarMensaje(
                                Alert.AlertType.INFORMATION,
                                "Matrícula eliminada correctamente.");

                    } catch (Exception ex) {

                        registrarLog(
                                "DELETE",
                                "ERROR",
                                ex.getMessage());

                        mostrarMensaje(
                                Alert.AlertType.ERROR,
                                ex.getMessage());
                    }
                }
            });
        }

        // ==========================================
        // TARJETA DE MATRÍCULAS
        // ==========================================

        Label tituloTabla = new Label(
                "Matrículas registradas");

        tituloTabla.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        Label textoBuscar = new Label("Buscar matrículas");

        textoBuscar.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        VBox zonaBusqueda = new VBox(
                8,
                textoBuscar,
                buscador);

        HBox botones = new HBox(10);

        botones.setAlignment(Pos.CENTER_LEFT);

        botones.getChildren().add(actualizar);

        if (esAdministrador) {
            botones.getChildren().add(eliminar);
        }

        VBox tarjetaTabla = new VBox(
                18,
                tituloTabla,
                zonaBusqueda,
                tabla,
                botones);

        tarjetaTabla.setPadding(new Insets(22));

        tarjetaTabla.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E1E7EC;" +
                "-fx-border-radius: 14;");

        VBox.setVgrow(tabla, Priority.ALWAYS);

        // ==========================================
        // CONTENIDO
        // ==========================================

        VBox contenido;

        if (esAdministrador) {

            contenido = new VBox(
                    22,
                    cabecera,
                    panelMatricula,
                    tarjetaTabla);

        } else {

            contenido = new VBox(
                    22,
                    cabecera,
                    tarjetaTabla);
        }

        contenido.setPadding(new Insets(30));

        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";");

        // ==========================================
        // CARGAR MATRÍCULAS
        // ==========================================

        cargarMatriculas(datosMatriculas);

        // ==========================================
        // VENTANA
        // ==========================================

        Scene escena = new Scene(
                contenido,
                1050,
                750);

        ventana.setScene(escena);

        ventana.setMinWidth(850);
        ventana.setMinHeight(600);

        ventana.setOnHidden(e -> {

            usuarioDAO.cerrar();
            cursoDAO.cerrar();
            servicio.cerrar();
        });

        ventana.show();
    }

    // ==========================================
    // CARGAR NOMBRES DE USUARIOS Y CURSOS
    // ==========================================

    private void cargarNombres() {

        nombresUsuarios.clear();
        nombresCursos.clear();

        for (Usuario usuario : usuarioDAO.listar()) {

            nombresUsuarios.put(
                    usuario.getId().toString(),
                    usuario.getNombre());
        }

        for (Curso curso : cursoDAO.listar()) {

            nombresCursos.put(
                    curso.getId().toString(),
                    curso.getNombre());
        }
    }

    // ==========================================
    // CREAR COLUMNAS
    // ==========================================

    private void crearColumnas() {

        tabla.getColumns().clear();

        TableColumn<Matricula, String> usuarioColumna =
                new TableColumn<>("Usuario");

        usuarioColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresUsuarios.getOrDefault(
                                d.getValue().getUsuarioId().toString(),
                                "Usuario desconocido")));

        TableColumn<Matricula, String> cursoColumna =
                new TableColumn<>("Curso");

        cursoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        nombresCursos.getOrDefault(
                                d.getValue().getCursoId().toString(),
                                "Curso desconocido")));

        TableColumn<Matricula, String> fechaColumna =
                new TableColumn<>("Fecha");

        fechaColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getFecha()));

        TableColumn<Matricula, String> estadoColumna =
                new TableColumn<>("Estado");

        estadoColumna.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()));

        usuarioColumna.setPrefWidth(180);
        cursoColumna.setPrefWidth(220);
        fechaColumna.setPrefWidth(130);
        estadoColumna.setPrefWidth(120);

        tabla.getColumns().addAll(
                usuarioColumna,
                cursoColumna,
                fechaColumna,
                estadoColumna);
    }

    // ==========================================
    // CARGAR MATRÍCULAS SEGÚN EL ROL
    // ==========================================

    private void cargarMatriculas(
            ObservableList<Matricula> datosMatriculas) {

        datosMatriculas.clear();

        if (esAdministrador) {

            for (Matricula matricula : servicio.listar()) {
                datosMatriculas.add(matricula);
            }

        } else {

            for (Matricula matricula :
                    servicio.listarPorUsuario(usuarioActual.getId())) {

                datosMatriculas.add(matricula);
            }
        }
    }

    // ==========================================
    // BUSCADOR
    // ==========================================

    private boolean contiene(String texto, String filtro) {

        return texto != null
                && texto.toLowerCase().contains(filtro);
    }

    // ==========================================
    // BOTONES
    // ==========================================

    private Button crearBoton(
            String texto,
            String color) {

        Button boton = new Button(texto);

        boton.setPrefHeight(40);

        boton.setPadding(
                new Insets(0, 18, 0, 18));

        boton.setCursor(Cursor.HAND);

        boton.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");

        return boton;
    }

    // ==========================================
    // MENSAJES
    // ==========================================

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
