package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.controlador.Navegador;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class EliminarCuentaController implements Initializable {

    private Gestor usuarioServicio;
    private Usuario usuarioActual;
    private boolean esAdministrador;

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private VBox bloqueAdmin;
    @FXML private ComboBox<Usuario> comboUsuarios;
    @FXML private VBox bloquePropio;
    @FXML private Label etiquetaNombre;
    @FXML private Label etiquetaEmail;
    @FXML private Button botonEliminar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "No hay ningún usuario conectado.");
            botonEliminar.setDisable(true);
            return;
        }

        usuarioServicio = new Gestor();
        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        if (esAdministrador) {

            bloqueAdmin.setVisible(true);
            bloqueAdmin.setManaged(true);

            comboUsuarios.setConverter(new StringConverter<Usuario>() {
                @Override
                public String toString(Usuario usuario) {
                    return usuario == null ? "" : usuario.getNombre() + " (" + usuario.getEmail() + ")";
                }

                @Override
                public Usuario fromString(String texto) {
                    return null;
                }
            });

            cargarUsuarios();

        } else {

            bloquePropio.setVisible(true);
            bloquePropio.setManaged(true);
            etiquetaNombre.setText("Nombre: " + usuarioActual.getNombre());
            etiquetaEmail.setText("Email: " + usuarioActual.getEmail());
        }

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> usuarioServicio.cerrar());
                    }
                });
            }
        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        comboUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML
    private void onEliminar() {

        Usuario usuario;

        if (esAdministrador) {

            usuario = comboUsuarios.getValue();

            if (usuario == null) {
                mostrarMensaje(Alert.AlertType.WARNING, "Selecciona un usuario.");
                return;
            }

        } else {
            usuario = usuarioActual;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar cuenta");
        confirmacion.setContentText("¿Seguro que quieres eliminar la cuenta de " + usuario.getNombre() + "?");

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            boolean resultado = usuarioServicio.eliminarUsuario(usuario.getId());

            if (resultado) {

                mostrarMensaje(Alert.AlertType.INFORMATION, "Cuenta eliminada correctamente.");

                if (esAdministrador) {
                    cargarUsuarios();
                } else {
                    SesionUsuario.cerrarSesion();
                    Navegador.abrirYCerrar(Navegador.LOGIN, contenido);
                }

            } else {
                mostrarMensaje(Alert.AlertType.ERROR, "No se ha podido eliminar la cuenta.");
            }
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
