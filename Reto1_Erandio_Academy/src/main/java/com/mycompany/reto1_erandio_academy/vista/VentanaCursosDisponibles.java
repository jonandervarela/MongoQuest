
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Curso;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.CursoServicio;
import com.mycompany.reto1_erandio_academy.servicio.MatriculaServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaCursosDisponibles {

    private final CursoServicio cursoServicio = new CursoServicio();
    private final MatriculaServicio matriculaServicio = new MatriculaServicio();

    public void mostrar() {

        Usuario usuario = SesionUsuario.getUsuarioActual();

        if (usuario == null) {
            mostrarError("No hay ningún usuario conectado.");
            cerrarServicios();
            return;
        }

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Cursos disponibles");

        Label titulo = new Label("Cursos disponibles");
        titulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

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

        tabla.getColumns().addAll(nombre, descripcion, duracion);

        Button matricular = new Button("Matricularme");
        Button actualizar = new Button("Actualizar cursos");

        // CARGAR CURSOS ACTIVOS
        cargarCursos(tabla);

        // MATRICULAR AL USUARIO
        matricular.setOnAction(e -> {

            Curso seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un curso.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Confirmar matrícula");
            confirmacion.setHeaderText("¿Quieres matricularte?");
            confirmacion.setContentText(
                    "Curso: " + seleccionado.getNombre());

            if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                    == ButtonType.OK) {

                try {

                    matriculaServicio.matricular(
                            usuario.getId(),
                            seleccionado.getId()
                    );

                    mostrarInformacion(
                            "Te has matriculado correctamente.");

                } catch (Exception ex) {

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR LISTA
        actualizar.setOnAction(e -> cargarCursos(tabla));

        VBox contenido = new VBox(
                15,
                titulo,
                tabla,
                matricular,
                actualizar
        );

        contenido.setPadding(new Insets(20));

        ventana.setScene(new Scene(contenido, 750, 450));

        ventana.setOnHidden(e -> cerrarServicios());

        ventana.show();
    }

    // CARGAR SOLAMENTE CURSOS ACTIVOS
    private void cargarCursos(TableView<Curso> tabla) {

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

    // MENSAJES
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

    // CERRAR CONEXIONES
    private void cerrarServicios() {
        cursoServicio.cerrar();
        matriculaServicio.cerrar();
    }
}
