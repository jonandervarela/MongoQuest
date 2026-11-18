package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;

public class RegistroUsuarioController implements Initializable {

    private final Gestor servicio = new Gestor();

    @FXML private GridPane formulario;
    @FXML private TextField nombre;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private TextField telefono;
    @FXML private Button registrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formulario.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> servicio.cerrar());
                    }
                });
            }
        });
    }

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "usuarios", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @FXML
    private void onRegistrar() {

        try {
            Usuario usuario = new Usuario(
                    null,
                    nombre.getText().trim(),
                    email.getText().trim(),
                    password.getText(),
                    telefono.getText().trim()
            );

            servicio.registrarUsuario(usuario);
            registrarLog("INSERT", "OK", null);
            mostrarMensaje(Alert.AlertType.INFORMATION, "Usuario registrado correctamente.");

            nombre.clear();
            email.clear();
            password.clear();
            telefono.clear();

        } catch (Exception ex) {
            registrarLog("INSERT", "ERROR", ex.getMessage());
            mostrarMensaje(Alert.AlertType.ERROR, ex.getMessage());
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
