package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;

public class PerfilUsuarioController implements Initializable {

    private DAOImpl usuarioDAO;
    private Usuario usuarioActual;

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private TextField campoNombre;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefono;
    @FXML private Button botonActualizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "No hay ningún usuario conectado.");
            botonActualizar.setDisable(true);
            return;
        }

        usuarioDAO = new DAOImpl();

        campoNombre.setText(usuarioActual.getNombre());
        campoEmail.setText(usuarioActual.getEmail());
        campoTelefono.setText(usuarioActual.getTelefono());

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> usuarioDAO.cerrar());
                    }
                });
            }
        });
    }

    @FXML
    private void onActualizar() {

        String nombre = campoNombre.getText().trim();
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Todos los campos son obligatorios.");
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setEmail(email);
        usuarioActual.setTelefono(telefono);

        Gestor logServicio = new Gestor();

        try {
            boolean resultado = usuarioDAO.modificarUsuario(usuarioActual);

            if (resultado) {
                logServicio.registrarLog("UPDATE", "usuarios", "OK", null);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Tu perfil se ha actualizado correctamente.");
            } else {
                logServicio.registrarLog("UPDATE", "usuarios", "ERROR", "No se pudo actualizar el perfil.");
                mostrarMensaje(Alert.AlertType.WARNING, "No se ha podido actualizar el perfil.");
            }

        } catch (Exception ex) {
            logServicio.registrarLog("UPDATE", "usuarios", "ERROR", ex.getMessage());
            mostrarMensaje(Alert.AlertType.ERROR, "Error al actualizar el perfil: " + ex.getMessage());
        } finally {
            logServicio.cerrar();
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
