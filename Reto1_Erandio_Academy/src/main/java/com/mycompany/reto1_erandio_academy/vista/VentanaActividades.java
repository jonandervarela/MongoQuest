
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Actividad;
import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.dao.CursoDAO;
import com.mycompany.reto1_erandio_academy.servicio.ActividadServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaActividades {

    private final ActividadServicio servicio = new ActividadServicio();
    private final CursoDAO cursoDAO = new CursoDAO();

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

        ComboBox<Curso> cursos = new ComboBox<>();

        cursos.setPromptText("Selecciona un curso");

        cursos.setItems(
                FXCollections.observableArrayList(cursoDAO.listar())
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

        tabla.getColumns().clear();
        tabla.getColumns().addAll(titulo, descripcion);

        Button nuevo = new Button("Nueva actividad");
        Button modificar = new Button("Modificar actividad");
        Button eliminar = new Button("Eliminar actividad");
        Button actualizar = new Button("Actualizar lista");

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

            GridPane formulario = crearFormulario(
                    tituloCampo, descripcionCampo);

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
                            curso.getId()
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

            GridPane formulario = crearFormulario(
                    tituloCampo, descripcionCampo);

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
                            seleccionada.getCursoId()
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

        cargarActividades(cursos);

        ventana.setScene(new Scene(contenido, 750, 550));

        ventana.setOnHidden(e -> {
            servicio.cerrar();
            cursoDAO.cerrar();
        });

        ventana.show();
    }

    private GridPane crearFormulario(
            TextField titulo, TextField descripcion) {

        GridPane formulario = new GridPane();
        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(titulo, 1, 0);

        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);

        return formulario;
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
}
