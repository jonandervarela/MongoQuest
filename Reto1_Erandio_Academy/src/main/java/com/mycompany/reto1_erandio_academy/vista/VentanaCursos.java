
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaCursos {

    private final CursoServicio servicio = new CursoServicio();

    // REGISTRAR LOG EN MONGODB
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        System.out.println("===== SESION EN CURSOS =====");
        System.out.println("Hay sesión: " + SesionUsuario.haySesion());

        if (SesionUsuario.getUsuarioActual() != null) {
            System.out.println("Usuario: "
                    + SesionUsuario.getUsuarioActual().getNombre());

            System.out.println("Email: "
                    + SesionUsuario.getUsuarioActual().getEmail());
        } else {
            System.out.println("USUARIO ACTUAL ES NULL");
        }

        System.out.println("============================");

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "cursos",
                    resultado,
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Gestión de cursos");

        TableView<Curso> tabla = new TableView<>();

        TableColumn<Curso, String> nombre =
                new TableColumn<>("Nombre");
        nombre.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));

        TableColumn<Curso, String> descripcion =
                new TableColumn<>("Descripción");
        descripcion.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("descripcion"));

        TableColumn<Curso, String> duracion =
                new TableColumn<>("Duración");
        duracion.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("duracion"));

        TableColumn<Curso, Boolean> activo =
                new TableColumn<>("Activo");
        activo.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("activo"));

        tabla.getColumns().addAll(nombre, descripcion, duracion, activo);

        Button nuevo = new Button("Registrar curso");
        Button modificar = new Button("Modificar curso");
        Button eliminar = new Button("Eliminar curso");
        Button actualizar = new Button("Actualizar lista");

        // REGISTRAR CURSO
        nuevo.setOnAction(e -> {

            TextField nombreCampo = new TextField();
            TextField descripcionCampo = new TextField();
            TextField duracionCampo = new TextField();

            CheckBox activoCampo = new CheckBox("Curso activo");
            activoCampo.setSelected(true);

            GridPane formulario = crearFormulario(
                    nombreCampo,
                    descripcionCampo,
                    duracionCampo,
                    activoCampo
            );

            Dialog<ButtonType> dialogo = crearDialogo(
                    "Registrar curso",
                    formulario
            );

            Optional<ButtonType> resultado = dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {
                    Curso curso = new Curso(
                            null,
                            nombreCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            duracionCampo.getText().trim(),
                            activoCampo.isSelected()
                    );

                    servicio.registrar(curso);

                    registrarLog("INSERT", "OK", null);

                    cargarCursos(tabla);
                    mostrarInformacion("Curso registrado correctamente.");

                } catch (Exception ex) {

                    registrarLog("INSERT", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // MODIFICAR CURSO
        modificar.setOnAction(e -> {

            Curso seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un curso.");
                return;
            }

            TextField nombreCampo =
                    new TextField(seleccionado.getNombre());

            TextField descripcionCampo =
                    new TextField(seleccionado.getDescripcion());

            TextField duracionCampo =
                    new TextField(seleccionado.getDuracion());

            CheckBox activoCampo = new CheckBox("Curso activo");
            activoCampo.setSelected(seleccionado.isActivo());

            GridPane formulario = crearFormulario(
                    nombreCampo,
                    descripcionCampo,
                    duracionCampo,
                    activoCampo
            );

            Dialog<ButtonType> dialogo = crearDialogo(
                    "Modificar curso",
                    formulario
            );

            Optional<ButtonType> resultado = dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {
                    Curso actualizado = new Curso(
                            seleccionado.getId(),
                            nombreCampo.getText().trim(),
                            descripcionCampo.getText().trim(),
                            duracionCampo.getText().trim(),
                            activoCampo.isSelected()
                    );

                    if (servicio.modificar(actualizado)) {

                        registrarLog("UPDATE", "OK", null);

                        cargarCursos(tabla);
                        mostrarInformacion("Curso modificado correctamente.");

                    } else {
                        mostrarAviso("No se encontraron cambios para guardar.");
                    }

                } catch (Exception ex) {

                    registrarLog("UPDATE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ELIMINAR CURSO
        eliminar.setOnAction(e -> {

            Curso seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un curso.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar curso");
            confirmacion.setHeaderText("¿Estás seguro?");
            confirmacion.setContentText(
                    "Vas a eliminar el curso: "
                    + seleccionado.getNombre()
            );

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {
                    if (servicio.eliminar(seleccionado.getId())) {

                        registrarLog("DELETE", "OK", null);

                        cargarCursos(tabla);
                        mostrarInformacion("Curso eliminado correctamente.");

                    } else {
                        mostrarAviso("No se ha encontrado el curso.");
                    }

                } catch (Exception ex) {

                    registrarLog("DELETE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR LISTA
        actualizar.setOnAction(e -> cargarCursos(tabla));

        cargarCursos(tabla);

        VBox contenido = new VBox(
                12,
                tabla,
                nuevo,
                modificar,
                eliminar,
                actualizar
        );

        contenido.setPadding(new Insets(15));

        ventana.setScene(new Scene(contenido, 850, 550));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    private GridPane crearFormulario(
            TextField nombre,
            TextField descripcion,
            TextField duracion,
            CheckBox activo) {

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(15));
        formulario.setHgap(10);
        formulario.setVgap(10);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcion, 1, 1);

        formulario.add(new Label("Duración:"), 0, 2);
        formulario.add(duracion, 1, 2);

        formulario.add(activo, 1, 3);

        return formulario;
    }

    private Dialog<ButtonType> crearDialogo(
            String titulo,
            GridPane formulario) {

        Dialog<ButtonType> dialogo = new Dialog<>();

        dialogo.setTitle(titulo);
        dialogo.getDialogPane().setContent(formulario);

        dialogo.getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        return dialogo;
    }

    private void cargarCursos(TableView<Curso> tabla) {

        try {
            tabla.setItems(
                    FXCollections.observableArrayList(
                            servicio.listar()
                    )
            );
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Atención");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Operación correcta");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Ha ocurrido un error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
