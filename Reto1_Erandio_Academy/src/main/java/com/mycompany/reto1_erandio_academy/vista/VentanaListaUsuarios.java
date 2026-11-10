
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class VentanaListaUsuarios {

    private final UsuarioServicio servicio = new UsuarioServicio();

    // REGISTRAR LOG
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "usuarios",
                    resultado,
                    error);
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Gestión de usuarios");

        TableView<Usuario> tabla = new TableView<>();

        TableColumn<Usuario, String> nombre =
                new TableColumn<>("Nombre");

        nombre.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> email =
                new TableColumn<>("Email");

        email.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("email"));

        TableColumn<Usuario, String> telefono =
                new TableColumn<>("Teléfono");

        telefono.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("telefono"));

        tabla.getColumns().addAll(nombre, email, telefono);

        Button modificar = new Button("Modificar usuario");
        Button eliminar = new Button("Eliminar usuario");
        Button actualizar = new Button("Actualizar lista");

        // MODIFICAR USUARIO
        modificar.setOnAction(e -> {

            Usuario seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un usuario.");
                return;
            }

            TextField nombreCampo =
                    new TextField(seleccionado.getNombre());

            TextField emailCampo =
                    new TextField(seleccionado.getEmail());

            TextField telefonoCampo =
                    new TextField(seleccionado.getTelefono());

            GridPane formulario = new GridPane();

            formulario.setPadding(new Insets(15));
            formulario.setHgap(10);
            formulario.setVgap(10);

            formulario.add(new Label("Nombre:"), 0, 0);
            formulario.add(nombreCampo, 1, 0);

            formulario.add(new Label("Email:"), 0, 1);
            formulario.add(emailCampo, 1, 1);

            formulario.add(new Label("Teléfono:"), 0, 2);
            formulario.add(telefonoCampo, 1, 2);

            Dialog<ButtonType> dialogo = new Dialog<>();

            dialogo.setTitle("Modificar usuario");
            dialogo.getDialogPane().setContent(formulario);

            dialogo.getDialogPane().getButtonTypes().addAll(
                    ButtonType.OK,
                    ButtonType.CANCEL
            );

            Optional<ButtonType> resultado =
                    dialogo.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    Usuario actualizado = new Usuario(
                            seleccionado.getId(),
                            nombreCampo.getText().trim(),
                            emailCampo.getText().trim(),
                            seleccionado.getPassword(),
                            telefonoCampo.getText().trim()
                    );

                    if (servicio.modificar(actualizado)) {

                        registrarLog("UPDATE", "OK", null);

                        cargarUsuarios(tabla);

                        mostrarInformacion(
                                "Usuario modificado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se han realizado cambios.");
                    }

                } catch (Exception ex) {

                    registrarLog("UPDATE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ELIMINAR USUARIO
        eliminar.setOnAction(e -> {

            Usuario seleccionado =
                    tabla.getSelectionModel().getSelectedItem();

            if (seleccionado == null) {
                mostrarAviso("Selecciona un usuario.");
                return;
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setTitle("Eliminar usuario");
            confirmacion.setHeaderText("¿Estás seguro?");
            confirmacion.setContentText(
                    "Vas a eliminar al usuario: "
                    + seleccionado.getNombre());

            Optional<ButtonType> resultado =
                    confirmacion.showAndWait();

            if (resultado.isPresent()
                    && resultado.get() == ButtonType.OK) {

                try {

                    boolean eliminado =
                            servicio.eliminar(seleccionado.getId());

                    if (eliminado) {

                        registrarLog("DELETE", "OK", null);

                        cargarUsuarios(tabla);

                        mostrarInformacion(
                                "Usuario eliminado correctamente.");

                    } else {

                        mostrarAviso(
                                "No se ha encontrado el usuario.");
                    }

                } catch (Exception ex) {

                    registrarLog("DELETE", "ERROR", ex.getMessage());

                    mostrarError(ex.getMessage());
                }
            }
        });

        // ACTUALIZAR LISTA
        actualizar.setOnAction(e -> cargarUsuarios(tabla));

        cargarUsuarios(tabla);

        VBox contenido = new VBox(
                12,
                tabla,
                modificar,
                eliminar,
                actualizar
        );

        contenido.setPadding(new Insets(15));

        ventana.setScene(new Scene(contenido, 700, 450));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    private void cargarUsuarios(TableView<Usuario> tabla) {

        try {

            tabla.setItems(
                    FXCollections.observableArrayList(
                            servicio.listar())
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
