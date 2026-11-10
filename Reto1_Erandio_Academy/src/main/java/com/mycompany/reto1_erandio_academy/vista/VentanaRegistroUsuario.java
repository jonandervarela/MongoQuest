
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VentanaRegistroUsuario {

    private final UsuarioServicio servicio = new UsuarioServicio();

    // REGISTRAR LOG EN MONGODB
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
        ventana.setTitle("Academy - Registrar usuario");

        TextField nombre = new TextField();
        TextField email = new TextField();
        PasswordField password = new PasswordField();
        TextField telefono = new TextField();

        nombre.setPromptText("Introduce tu nombre");
        email.setPromptText("Introduce tu email");
        password.setPromptText("Mínimo 6 caracteres");
        telefono.setPromptText("Introduce tu teléfono");

        Button registrar = new Button("Registrar usuario");

        registrar.setOnAction(e -> {

            try {

                Usuario usuario = new Usuario(
                        null,
                        nombre.getText().trim(),
                        email.getText().trim(),
                        password.getText(),
                        telefono.getText().trim()
                );

                servicio.registrar(usuario);

                // REGISTRAR OPERACIÓN
                registrarLog("INSERT", "OK", null);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Usuario registrado correctamente.");

                nombre.clear();
                email.clear();
                password.clear();
                telefono.clear();

            } catch (Exception ex) {

                // REGISTRAR ERROR
                registrarLog("INSERT", "ERROR", ex.getMessage());

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        ex.getMessage());
            }
        });

        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(20));
        formulario.setHgap(10);
        formulario.setVgap(15);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Email:"), 0, 1);
        formulario.add(email, 1, 1);

        formulario.add(new Label("Contraseña:"), 0, 2);
        formulario.add(password, 1, 2);

        formulario.add(new Label("Teléfono:"), 0, 3);
        formulario.add(telefono, 1, 3);

        formulario.add(registrar, 1, 4);

        ventana.setScene(new Scene(formulario, 450, 300));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    private void mostrarMensaje(
            Alert.AlertType tipo, String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
