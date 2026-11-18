package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class ListaUsuariosController implements Initializable {

    private final Gestor servicio = new Gestor();

    @FXML private VBox contenido;
    @FXML private TableView<Usuario> tabla;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private Button modificar;
    @FXML private Button eliminar;
    @FXML private Button actualizar;

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "usuarios", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colNombre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telefono"));

        cargarUsuarios();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> servicio.cerrar());
                    }
                });
            }
        });
    }

    @FXML
    private void onModificar() {

        Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un usuario.");
            return;
        }

        TextField nombreCampo = new TextField(seleccionado.getNombre());
        TextField emailCampo = new TextField(seleccionado.getEmail());
        TextField telefonoCampo = new TextField(seleccionado.getTelefono());

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
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                Usuario actualizado = new Usuario(
                        seleccionado.getId(),
                        nombreCampo.getText().trim(),
                        emailCampo.getText().trim(),
                        seleccionado.getPassword(),
                        telefonoCampo.getText().trim()
                );

                if (servicio.modificarUsuario(actualizado)) {
                    registrarLog("UPDATE", "OK", null);
                    cargarUsuarios();
                    mostrarInformacion("Usuario modificado correctamente.");
                } else {
                    mostrarAviso("No se han realizado cambios.");
                }

            } catch (Exception ex) {
                registrarLog("UPDATE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onEliminar() {

        Usuario seleccionado = tabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAviso("Selecciona un usuario.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar usuario");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Vas a eliminar al usuario: " + seleccionado.getNombre());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {

            try {
                boolean eliminado = servicio.eliminarUsuario(seleccionado.getId());

                if (eliminado) {
                    registrarLog("DELETE", "OK", null);
                    cargarUsuarios();
                    mostrarInformacion("Usuario eliminado correctamente.");
                } else {
                    mostrarAviso("No se ha encontrado el usuario.");
                }

            } catch (Exception ex) {
                registrarLog("DELETE", "ERROR", ex.getMessage());
                mostrarError(ex.getMessage());
            }
        }
    }

    @FXML
    private void onActualizar() {
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            tabla.setItems(FXCollections.observableArrayList(servicio.listarUsuarios()));
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
