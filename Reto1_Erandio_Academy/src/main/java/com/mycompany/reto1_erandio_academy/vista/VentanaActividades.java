package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Ejercicio;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.servicio.ActividadServicio;
import com.mycompany.reto1_erandio_academy.servicio.AsignacionServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VentanaActividades {

    private final ActividadServicio servicio = new ActividadServicio();
    private final CursoDAO cursoDAO = new CursoDAO();
    // NUEVO: para saber qué cursos imparte el profesor que ha entrado
    private final AsignacionServicio asignacionServicio = new AsignacionServicio();

    private final TableView<Actividad> tabla = new TableView<>();

    // REGISTRAR LOG EN MONGODB
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

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Gestión de actividades");

        // NUEVO: solo se ven los cursos que puede tocar quien ha entrado
        List<Curso> cursosPermitidos = cursosDelUsuarioActual();

        ComboBox<Curso> cursos = new ComboBox<>();

        cursos.setPromptText("Selecciona un curso");

        cursos.setItems(
                FXCollections.observableArrayList(cursosPermitidos)
        );

        cursos.setCellFactory(lista -> new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null
                        ? null : curso.getNombre());
            }
        });

        cursos.setButtonCell(new ListCell<Curso>() {
            @Override
            protected void updateItem(Curso curso, boolean vacio) {
                super.updateItem(curso, vacio);
                setText(vacio || curso == null
                        ? null : curso.getNombre());
            }
        });

        TableColumn<Actividad, String> titulo =
                new TableColumn<>("Título");

        titulo.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("titulo"));

        TableColumn<Actividad, String> descripcion =
                new TableColumn<>("Descripción");

        descripcion.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));

        // NUEVO: columna para ver de un vistazo cuántos ejercicios tiene
        TableColumn<Actividad, Number> numEjercicios =
                new TableColumn<>("Ejercicios");

        numEjercicios.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleIntegerProperty(
                        datos.getValue().getEjercicios() == null
                                ? 0 : datos.getValue().getEjercicios().size()
                ));

        tabla.getColumns().clear();
        tabla.getColumns().addAll(titulo, descripcion, numEjercicios);

        Button nuevo = new Button("Nueva actividad");
        Button modificar = new Button("Modificar actividad");
        Button eliminar = new Button("Eliminar actividad");
        Button actualizar = new Button("Actualizar lista");

        estiloBoton(nuevo, AZUL);
        estiloBoton(modificar, AZUL);
        estiloBoton(eliminar, ROJO);
        estiloBoton(actualizar, AZUL);

        // Si no hay ningún curso disponible, no tiene sentido crear nada
        if (cursosPermitidos.isEmpty()) {
            nuevo.setDisable(true);
        }

        // FILTRAR POR CURSO
        cursos.setOnAction(e -> cargarActividades(cursos));

        // REGISTRAR ACTIVIDAD
        nuevo.setOnAction(e -> {

            Curso curso = cursos.getValue();

            if (curso == null) {
                mostrarMensaje("Selecciona un curso.");
                return;
            }

            TextField tituloCampo = new TextField();
            TextField descripcionCampo = new TextField();
            PanelEjercicios panelEjercicios =
                    crearPanelEjercicios(new ArrayList<>());

            GridPane formulario = crearFormulario(
                    tituloCampo, descripcionCampo, panelEjercicios.nodo);

            Dialog<ButtonType> dialogo = new Dialog<>();
            dialogo.setTitle("Nueva actividad");
            dialogo.getDialogPane().setContent(formulario);
            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> resultado = dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {
                    Actividad actividad = new Actividad(
                            null,
                            tituloCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            curso.getId(),
                            new ArrayList<>(panelEjercicios.lista)
                    );

                    servicio.registrar(actividad);

                    // REGISTRAR LOG
                    registrarLog("INSERT", "OK", null);

                    cargarActividades(cursos);
                    mostrarMensaje("Actividad registrada correctamente.");

                } catch (Exception ex) {

                    registrarLog("INSERT", "ERROR", ex.getMessage());

                    mostrarMensaje(ex.getMessage());
                }
            }
        });

        // MODIFICAR ACTIVIDAD
        modificar.setOnAction(e -> {

            Actividad seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                mostrarMensaje("Selecciona una actividad.");
                return;
            }

            TextField tituloCampo =
                    new TextField(seleccionada.getTitulo());

            TextField descripcionCampo =
                    new TextField(seleccionada.getDescripcion());

            PanelEjercicios panelEjercicios = crearPanelEjercicios(
                    seleccionada.getEjercicios() == null
                            ? new ArrayList<>()
                            : seleccionada.getEjercicios());

            GridPane formulario = crearFormulario(
                    tituloCampo, descripcionCampo, panelEjercicios.nodo);

            Dialog<ButtonType> dialogo = new Dialog<>();
            dialogo.setTitle("Modificar actividad");
            dialogo.getDialogPane().setContent(formulario);
            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> resultado = dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {
                    Actividad actualizada = new Actividad(
                            seleccionada.getId(),
                            tituloCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            seleccionada.getCursoId(),
                            new ArrayList<>(panelEjercicios.lista)
                    );

                    servicio.modificar(actualizada);

                    // REGISTRAR LOG
                    registrarLog("UPDATE", "OK", null);

                    cargarActividades(cursos);
                    mostrarMensaje("Actividad modificada correctamente.");

                } catch (Exception ex) {

                    registrarLog("UPDATE", "ERROR", ex.getMessage());

                    mostrarMensaje(ex.getMessage());
                }
            }
        });

        // ELIMINAR ACTIVIDAD
        eliminar.setOnAction(e -> {

            Actividad seleccionada =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                mostrarMensaje("Selecciona una actividad.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar actividad");
            confirmacion.setHeaderText("¿Estás seguro?");
            confirmacion.setContentText(seleccionada.getTitulo());

            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                    == ButtonType.OK) {

                try {
                    servicio.eliminar(seleccionada.getId());

                    // REGISTRAR LOG
                    registrarLog("DELETE", "OK", null);

                    cargarActividades(cursos);
                    mostrarMensaje("Actividad eliminada correctamente.");

                } catch (Exception ex) {

                    registrarLog("DELETE", "ERROR", ex.getMessage());

                    mostrarMensaje(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR LISTA
        actualizar.setOnAction(e -> cargarActividades(cursos));

        VBox contenido = new VBox(
                12,
                new Label("Seleccionar curso:"),
                cursos,
                tabla,
                nuevo,
                modificar,
                eliminar,
                actualizar
        );

        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: #F2F5F7;");

        // NUEVO: aviso si el profesor no tiene ningún curso asignado
        if (esEmpleado() && cursosPermitidos.isEmpty()) {
            Label aviso = new Label(
                    "No tienes ningún curso asignado todavía. "
                    + "Pide al administrador que te asigne uno.");
            contenido.getChildren().add(0, aviso);
        }

        cargarActividades(cursos);

        ventana.setScene(new Scene(contenido, 750, 600));

        ventana.setOnHidden(e -> {
            servicio.cerrar();
            cursoDAO.cerrar();
            asignacionServicio.cerrar();
        });

        ventana.show();
    }

    // NUEVO: cursos que puede gestionar el usuario que ha entrado.
    // Admin ve todos los cursos; un empleado solo ve los suyos.
    private List<Curso> cursosDelUsuarioActual() {

        List<Curso> todos = cursoDAO.listar();

        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null || !esEmpleado()) {
            // Admin (o sesión no identificada): sin filtro
            return todos;
        }

        if (usuario.getEmpleadoId() == null) {
            // Cuenta de empleado sin ficha de empleado enlazada
            return new ArrayList<>();
        }

        Set<ObjectId> cursosAsignados = new HashSet<>();

        for (Document asignacion
                : asignacionServicio.listarPorEmpleado(usuario.getEmpleadoId())) {

            cursosAsignados.add(asignacion.getObjectId("cursoId"));
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

    private GridPane crearFormulario(
            TextField titulo, TextField descripcion, javafx.scene.Node panelEjercicios) {

        GridPane formulario = new GridPane();
        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(titulo, 1, 0);

        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);

        // NUEVO: ejercicios de la actividad
        formulario.add(new Label("Ejercicios:"), 0, 2);
        formulario.add(panelEjercicios, 1, 2);

        return formulario;
    }

    // NUEVO: lista editable de ejercicios (añadir / quitar preguntas)
    private PanelEjercicios crearPanelEjercicios(List<Ejercicio> iniciales) {

        ObservableList<Ejercicio> lista =
                FXCollections.observableArrayList(iniciales);

        ListView<Ejercicio> listaVisual = new ListView<>(lista);
        listaVisual.setPrefHeight(100);
        listaVisual.setPrefWidth(260);

        TextField nuevaPregunta = new TextField();
        nuevaPregunta.setPromptText("Pregunta del ejercicio");

        Button anadir = new Button("Añadir");
        estiloBoton(anadir, AZUL);
        anadir.setOnAction(e -> {

            String texto = nuevaPregunta.getText().trim();

            if (!texto.isEmpty()) {
                lista.add(new Ejercicio(texto));
                nuevaPregunta.clear();
            }
        });

        Button quitar = new Button("Quitar seleccionado");
        estiloBoton(quitar, ROJO);
        quitar.setOnAction(e -> {

            Ejercicio seleccionado =
                    listaVisual.getSelectionModel().getSelectedItem();

            if (seleccionado != null) {
                lista.remove(seleccionado);
            }
        });

        HBox fila = new HBox(8, nuevaPregunta, anadir, quitar);

        VBox contenedor = new VBox(6, listaVisual, fila);

        return new PanelEjercicios(contenedor, lista);
    }

    // Pequeña envoltura para devolver a la vez el panel visual y su lista
    private static class PanelEjercicios {

        final VBox nodo;
        final ObservableList<Ejercicio> lista;

        PanelEjercicios(VBox nodo, ObservableList<Ejercicio> lista) {
            this.nodo = nodo;
            this.lista = lista;
        }
    }

    private void cargarActividades(ComboBox<Curso> cursos) {

        Curso curso = cursos.getValue();

        if (curso == null) {
            tabla.getItems().clear();
            return;
        }

        tabla.setItems(
                FXCollections.observableArrayList(
                        servicio.listarPorCurso(curso.getId())
                )
        );
    }

    private void mostrarMensaje(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // ---------- Estilo visual (misma técnica que el resto de la app) ----------
    private static final String AZUL = "#1F6F8B";
    private static final String ROJO = "#B3382C";

    private void estiloBoton(Button boton, String colorAcento) {

        String base =
                "-fx-background-color: white;"
                + "-fx-text-fill: #12283A;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D5DDE3;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        String encima =
                "-fx-background-color: " + colorAcento + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(base));
    }
}
