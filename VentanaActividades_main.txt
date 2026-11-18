
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Ejercicio;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.servicio.ActividadServicio;
import com.mycompany.reto1_erandio_academy.servicio.AsignacionServicio;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

public class VentanaActividades {

    private final ActividadServicio servicio =
            new ActividadServicio();

    private final CursoDAO cursoDAO =
            new CursoDAO();

    private final AsignacionServicio asignacionServicio =
            new AsignacionServicio();

    private final TableView<Actividad> tabla =
            new TableView<>();

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
                    "actividades",
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

        Stage ventana = new Stage();

        ventana.setTitle(
                "Erandio Academy - Gestión de actividades");

        // ==========================================
        // LISTA DE ACTIVIDADES
        // ==========================================

        ObservableList<Actividad> datosActividades =
                FXCollections.observableArrayList();

        FilteredList<Actividad> actividadesFiltradas =
                new FilteredList<>(datosActividades, a -> true);

        tabla.setItems(actividadesFiltradas);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);

        tabla.setPlaceholder(
                new Label("Selecciona un curso para ver sus actividades."));

        // ==========================================
        // SELECTOR DE CURSOS
        // ==========================================

        ComboBox<Curso> cursos = new ComboBox<>();

        cursos.setPromptText("Selecciona un curso");
        cursos.setMaxWidth(Double.MAX_VALUE);
        cursos.setPrefHeight(42);

        List<Curso> cursosPermitidos = cursosDelUsuarioActual();

        cursos.setItems(
                FXCollections.observableArrayList(cursosPermitidos));

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

        // ==========================================
        // COLUMNAS DE LA TABLA
        // ==========================================

        TableColumn<Actividad, String> tituloColumna =
                new TableColumn<>("Título");

        tituloColumna.setCellValueFactory(
                new PropertyValueFactory<>("titulo"));

        TableColumn<Actividad, String> descripcionColumna =
                new TableColumn<>("Descripción");

        descripcionColumna.setCellValueFactory(
                new PropertyValueFactory<>("descripcion"));

        TableColumn<Actividad, Number> numEjercicios =
                new TableColumn<>("Ejercicios");

        numEjercicios.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleIntegerProperty(
                        datos.getValue().getEjercicios() == null
                                ? 0 : datos.getValue().getEjercicios().size()));

        tituloColumna.setPrefWidth(250);
        descripcionColumna.setPrefWidth(500);

        tabla.getColumns().clear();

        tabla.getColumns().addAll(
                tituloColumna,
                descripcionColumna,
                numEjercicios);

        // ==========================================
        // BUSCADOR
        // ==========================================

        TextField buscador = new TextField();

        buscador.setPromptText(
                "Buscar por título o descripción...");

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

            actividadesFiltradas.setPredicate(actividad -> {

                if (filtro.isEmpty()) {
                    return true;
                }

                return contiene(
                        actividad.getTitulo(), filtro)
                        || contiene(
                        actividad.getDescripcion(), filtro);
            });
        });

        // ==========================================
        // BOTONES
        // ==========================================

        Button nuevo = crearBoton(
                "Nueva actividad", AZUL);

        Button modificar = crearBoton(
                "Modificar actividad", AZUL_OSCURO);

        Button eliminar = crearBoton(
                "Eliminar actividad", "#C0392B");

        Button actualizar = crearBoton(
                "Actualizar lista", AZUL_OSCURO);

        if (cursosPermitidos.isEmpty() && esEmpleado()) {
            nuevo.setDisable(true);
        }

        // ==========================================
        // FILTRAR POR CURSO
        // ==========================================

        cursos.setOnAction(e -> {

            buscador.clear();

            cargarActividades(
                    cursos,
                    datosActividades);
        });

        // ==========================================
        // NUEVA ACTIVIDAD
        // ==========================================

        nuevo.setOnAction(e -> {

            Curso curso = cursos.getValue();

            if (curso == null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Selecciona un curso.");

                return;
            }

            TextField tituloCampo = new TextField();

            tituloCampo.setPromptText(
                    "Introduce el título de la actividad");

            TextArea descripcionCampo = new TextArea();

            descripcionCampo.setPromptText(
                    "Introduce la descripción de la actividad");

            descripcionCampo.setPrefRowCount(4);
            descripcionCampo.setWrapText(true);

            PanelEjercicios panelEjercicios =
                    crearPanelEjercicios(new ArrayList<>());

            GridPane formulario = crearFormulario(
                    tituloCampo,
                    descripcionCampo,
                    panelEjercicios.nodo);

            Dialog<ButtonType> dialogo = new Dialog<>();

            dialogo.setTitle("Nueva actividad");

            dialogo.setHeaderText(
                    "Registrar una nueva actividad");

            dialogo.getDialogPane().setContent(formulario);

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL);

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                if (tituloCampo.getText().trim().isEmpty()
                        || descripcionCampo.getText().trim().isEmpty()) {

                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Completa el título y la descripción.");

                    return;
                }

                try {

                    Actividad actividad = new Actividad(
                            null,
                            tituloCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            curso.getId(),
                            new ArrayList<>(panelEjercicios.lista));

                    servicio.registrar(actividad);

                    registrarLog("INSERT", "OK", null);

                    cargarActividades(
                            cursos,
                            datosActividades);

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Actividad registrada correctamente.");

                } catch (Exception ex) {

                    registrarLog(
                            "INSERT",
                            "ERROR",
                            ex.getMessage());

                    mostrarMensaje(
                            Alert.AlertType.ERROR,
                            ex.getMessage());
                }
            }
        });

        // ==========================================
        // MODIFICAR ACTIVIDAD
        // ==========================================

        modificar.setOnAction(e -> {

            Actividad seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Selecciona una actividad.");

                return;
            }

            TextField tituloCampo =
                    new TextField(seleccionada.getTitulo());

            TextArea descripcionCampo =
                    new TextArea(seleccionada.getDescripcion());

            descripcionCampo.setPrefRowCount(4);
            descripcionCampo.setWrapText(true);

            PanelEjercicios panelEjercicios = crearPanelEjercicios(
                    seleccionada.getEjercicios() == null
                            ? new ArrayList<>()
                            : seleccionada.getEjercicios());

            GridPane formulario = crearFormulario(
                    tituloCampo,
                    descripcionCampo,
                    panelEjercicios.nodo);

            Dialog<ButtonType> dialogo = new Dialog<>();

            dialogo.setTitle("Modificar actividad");

            dialogo.setHeaderText(
                    "Editar información de la actividad");

            dialogo.getDialogPane().setContent(formulario);

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL);

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                if (tituloCampo.getText().trim().isEmpty()
                        || descripcionCampo.getText().trim().isEmpty()) {

                    mostrarMensaje(
                            Alert.AlertType.WARNING,
                            "Completa el título y la descripción.");

                    return;
                }

                try {

                    Actividad actualizada = new Actividad(
                            seleccionada.getId(),
                            tituloCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            seleccionada.getCursoId(),
                            new ArrayList<>(panelEjercicios.lista));

                    servicio.modificar(actualizada);

                    registrarLog("UPDATE", "OK", null);

                    cargarActividades(
                            cursos,
                            datosActividades);

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Actividad modificada correctamente.");

                } catch (Exception ex) {

                    registrarLog(
                            "UPDATE",
                            "ERROR",
                            ex.getMessage());

                    mostrarMensaje(
                            Alert.AlertType.ERROR,
                            ex.getMessage());
                }
            }
        });

        // ==========================================
        // ELIMINAR ACTIVIDAD
        // ==========================================

        eliminar.setOnAction(e -> {

            Actividad seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Selecciona una actividad.");

                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar actividad");

            confirmacion.setHeaderText(
                    "Confirmar eliminación");

            confirmacion.setContentText(
                    "¿Estás seguro de eliminar la actividad: "
                    + seleccionada.getTitulo() + "?");

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    servicio.eliminar(seleccionada.getId());

                    registrarLog("DELETE", "OK", null);

                    cargarActividades(
                            cursos,
                            datosActividades);

                    mostrarMensaje(
                            Alert.AlertType.INFORMATION,
                            "Actividad eliminada correctamente.");

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

        // ==========================================
        // ACTUALIZAR LISTA
        // ==========================================

        actualizar.setOnAction(e -> {

            buscador.clear();

            cargarActividades(
                    cursos,
                    datosActividades);
        });

        // ==========================================
        // CABECERA
        // ==========================================

        Label etiqueta = new Label("ADMINISTRACIÓN ACADÉMICA");

        etiqueta.setStyle(
                "-fx-text-fill: " + AZUL + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label titulo = new Label("Gestión de actividades");

        titulo.setStyle(
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;");

        Label descripcion = new Label(
                "Crea, consulta, modifica y administra las actividades de cada curso.");

        descripcion.setStyle(
                "-fx-text-fill: " + GRIS + ";" +
                "-fx-font-size: 13px;");

        VBox cabecera = new VBox(
                6,
                etiqueta,
                titulo,
                descripcion);

        // ==========================================
        // PANEL DE SELECCIÓN DE CURSO
        // ==========================================

        Label tituloCurso = new Label("Seleccionar curso");

        tituloCurso.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        VBox panelCurso = new VBox(
                12,
                tituloCurso,
                cursos);

        panelCurso.setPadding(new Insets(20));

        panelCurso.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #E1E7EC;" +
                "-fx-border-radius: 14;");

        // ==========================================
        // PANEL DE ACTIVIDADES
        // ==========================================

        Label tituloTabla = new Label(
                "Actividades registradas");

        tituloTabla.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        Label textoBuscar = new Label("Buscar actividades");

        textoBuscar.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";");

        VBox zonaBusqueda = new VBox(
                8,
                textoBuscar,
                buscador);

        HBox botones = new HBox(
                10,
                nuevo,
                modificar,
                eliminar,
                actualizar);

        botones.setAlignment(Pos.CENTER_LEFT);
        botones.setSpacing(10);
        botones.setPadding(new Insets(5, 0, 0, 0));

        botones.setOnMouseEntered(e -> {
        });

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
        // CONTENIDO PRINCIPAL
        // ==========================================

        VBox contenido = new VBox(
                22,
                cabecera,
                panelCurso,
                tarjetaTabla);

        contenido.setPadding(new Insets(30));

        contenido.setStyle(
                "-fx-background-color: " + FONDO + ";");

        if (esEmpleado() && cursosPermitidos.isEmpty()) {
            Label aviso = new Label(
                    "No tienes ningún curso asignado todavía. "
                    + "Pide al administrador que te asigne uno.");
            aviso.setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
            contenido.getChildren().add(1, aviso);
        }

        // ==========================================
        // VENTANA
        // ==========================================

        Scene escena = new Scene(
                contenido,
                1100,
                750);

        ventana.setScene(escena);

        ventana.setMinWidth(850);
        ventana.setMinHeight(600);

        ventana.setOnHidden(e -> {
            servicio.cerrar();
            cursoDAO.cerrar();
            asignacionServicio.cerrar();
        });

        ventana.show();
    }

    // ==========================================
    // CREAR FORMULARIO
    // ==========================================

    private GridPane crearFormulario(
            TextField titulo,
            Control descripcion,
            javafx.scene.Node panelEjercicios) {

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(20));
        formulario.setHgap(15);
        formulario.setVgap(15);

        titulo.setPrefWidth(350);

        descripcion.setPrefWidth(350);

        formulario.add(
                new Label("Título:"),
                0,
                0);

        formulario.add(
                titulo,
                1,
                0);

        formulario.add(
                new Label("Descripción:"),
                0,
                1);

        formulario.add(
                descripcion,
                1,
                1);

        formulario.add(new Label("Ejercicios:"), 0, 2);
        formulario.add(panelEjercicios, 1, 2);

        return formulario;
    }

    // ==========================================
    // CURSOS PERMITIDOS PARA EL USUARIO ACTUAL
    // ==========================================

    private List<Curso> cursosDelUsuarioActual() {

        List<Curso> todos = cursoDAO.listar();
        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null || !esEmpleado()) {
            return todos;
        }

        if (usuario.getEmpleadoId() == null) {
            return new ArrayList<>();
        }

        Set<ObjectId> cursosAsignados = new HashSet<>();

        for (Document asignacion :
                asignacionServicio.listarPorEmpleado(usuario.getEmpleadoId())) {
            ObjectId cursoId = asignacion.getObjectId("cursoId");
            if (cursoId != null) {
                cursosAsignados.add(cursoId);
            }
        }

        List<Curso> permitidos = new ArrayList<>();

        for (Curso curso : todos) {
            if (cursosAsignados.contains(curso.getId())) {
                permitidos.add(curso);
            }
        }

        return permitidos;
    }

    private boolean esEmpleado() {
        return SesionUsuario.tieneRol("EMPLEADO");
    }

    // ==========================================
    // PANEL PARA GESTIONAR EJERCICIOS
    // ==========================================

    private PanelEjercicios crearPanelEjercicios(List<Ejercicio> iniciales) {

        ObservableList<Ejercicio> lista =
                FXCollections.observableArrayList(iniciales);

        ListView<Ejercicio> listaVisual = new ListView<>(lista);
        listaVisual.setPrefHeight(110);
        listaVisual.setPrefWidth(350);

        TextField nuevaPregunta = new TextField();
        nuevaPregunta.setPromptText("Pregunta del ejercicio");

        Button anadir = crearBoton("Añadir", AZUL);
        anadir.setOnAction(e -> {
            String texto = nuevaPregunta.getText().trim();
            if (!texto.isEmpty()) {
                lista.add(new Ejercicio(texto));
                nuevaPregunta.clear();
            }
        });

        Button quitar = crearBoton("Quitar seleccionado", "#C0392B");
        quitar.setOnAction(e -> {
            Ejercicio seleccionado =
                    listaVisual.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                lista.remove(seleccionado);
            }
        });

        HBox fila = new HBox(8, nuevaPregunta, anadir, quitar);
        VBox contenedor = new VBox(8, listaVisual, fila);

        return new PanelEjercicios(contenedor, lista);
    }

    private static class PanelEjercicios {
        final VBox nodo;
        final ObservableList<Ejercicio> lista;

        PanelEjercicios(VBox nodo, ObservableList<Ejercicio> lista) {
            this.nodo = nodo;
            this.lista = lista;
        }
    }

    // ==========================================
    // CARGAR ACTIVIDADES POR CURSO
    // ==========================================

    private void cargarActividades(
            ComboBox<Curso> cursos,
            ObservableList<Actividad> datosActividades) {

        Curso curso = cursos.getValue();

        datosActividades.clear();

        if (curso == null) {
            return;
        }

        try {

            for (Actividad actividad :
                    servicio.listarPorCurso(curso.getId())) {

                datosActividades.add(actividad);
            }

            tabla.refresh();

        } catch (Exception ex) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    ex.getMessage());
        }
    }

    // ==========================================
    // BUSCADOR
    // ==========================================

    private boolean contiene(
            String texto,
            String filtro) {

        return texto != null
                && texto.toLowerCase().contains(filtro);
    }

    // ==========================================
    // CREAR BOTONES
    // ==========================================

    private Button crearBoton(
            String texto,
            String color) {

        Button boton = new Button(texto);

        boton.setPrefHeight(40);

        boton.setPadding(
                new Insets(0, 16, 0, 16));

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
    // MOSTRAR MENSAJES
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
