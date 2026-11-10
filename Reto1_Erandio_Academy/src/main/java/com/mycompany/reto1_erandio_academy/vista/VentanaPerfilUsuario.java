
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaPerfilUsuario {

    private UsuarioDAO usuarioDAO;

    private TextField campoNombre;
    private TextField campoEmail;
    private TextField campoTelefono;

    private Usuario usuarioActual;

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado."
            );
            return;
        }

        usuarioDAO = new UsuarioDAO();

        Stage ventana = new Stage();

        Label titulo = new Label("MI PERFIL");
        titulo.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        Label etiquetaNombre = new Label("Nombre:");

        campoNombre = new TextField();
        campoNombre.setText(usuarioActual.getNombre());

        Label etiquetaEmail = new Label("Correo electrónico:");

        campoEmail = new TextField();
        campoEmail.setText(usuarioActual.getEmail());

        Label etiquetaTelefono = new Label("Teléfono:");

        campoTelefono = new TextField();
        campoTelefono.setText(usuarioActual.getTelefono());

        Button botonActualizar = new Button("Actualizar perfil");

        botonActualizar.setOnAction(e -> actualizarPerfil());

        VBox contenido = new VBox(
                15,
                titulo,
                etiquetaNombre,
                campoNombre,
                etiquetaEmail,
                campoEmail,
                etiquetaTelefono,
                campoTelefono,
                botonActualizar
        );

        contenido.setPadding(new Insets(25));

        Scene escena = new Scene(contenido, 450, 450);

        ventana.setTitle("Academy - Mi perfil");
        ventana.setScene(escena);
        ventana.show();

        ventana.setOnHidden(e -> usuarioDAO.cerrar());
    }

    private void actualizarPerfil() {

        String nombre = campoNombre.getText().trim();
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Todos los campos son obligatorios."
            );
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setEmail(email);
        usuarioActual.setTelefono(telefono);

        LogServicio logServicio = new LogServicio();

        try {

            boolean resultado = usuarioDAO.modificar(usuarioActual);

            if (resultado) {

                logServicio.registrar(
                        "UPDATE",
                        "usuarios",
                        "OK",
                        null
                );

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Tu perfil se ha actualizado correctamente."
                );

            } else {

                logServicio.registrar(
                        "UPDATE",
                        "usuarios",
                        "ERROR",
                        "No se pudo actualizar el perfil."
                );

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "No se ha podido actualizar el perfil."
                );
            }

        } catch (Exception ex) {

            logServicio.registrar(
                    "UPDATE",
                    "usuarios",
                    "ERROR",
                    ex.getMessage()
            );

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al actualizar el perfil: " + ex.getMessage()
            );

        } finally {
            logServicio.cerrar();
        }
    }

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
