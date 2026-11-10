
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaEliminarCuenta {

    private UsuarioServicio usuarioServicio;
    private ComboBox<Usuario> comboUsuarios;
    private Usuario usuarioActual;
    private Stage ventana;
    private boolean esAdministrador;

    public void mostrar() {

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No hay ningún usuario conectado."
            );
            return;
        }

        usuarioServicio = new UsuarioServicio();

        esAdministrador = SesionUsuario.tieneRol("ADMIN");

        ventana = new Stage();

        Label titulo = new Label("ELIMINAR CUENTA");
        titulo.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(30));

        contenido.getChildren().add(titulo);

        if (esAdministrador) {

            Label etiqueta = new Label("Seleccionar usuario:");

            comboUsuarios = new ComboBox<>();
            comboUsuarios.setPrefWidth(300);

            comboUsuarios.setConverter(new StringConverter<Usuario>() {

                @Override
                public String toString(Usuario usuario) {
                    return usuario == null ? "" :
                            usuario.getNombre()
                            + " (" + usuario.getEmail() + ")";
                }

                @Override
                public Usuario fromString(String texto) {
                    return null;
                }
            });

            cargarUsuarios();

            contenido.getChildren().addAll(
                    etiqueta,
                    comboUsuarios
            );

        } else {

            Label etiqueta = new Label(
                    "Vas a eliminar tu propia cuenta:"
            );

            Label nombre = new Label(
                    "Nombre: " + usuarioActual.getNombre()
            );

            Label email = new Label(
                    "Email: " + usuarioActual.getEmail()
            );

            contenido.getChildren().addAll(
                    etiqueta,
                    nombre,
                    email
            );
        }

        Button botonEliminar = new Button("Eliminar cuenta");

        botonEliminar.setOnAction(e -> eliminarCuenta());

        contenido.getChildren().add(botonEliminar);

        Scene escena = new Scene(contenido, 450, 350);

        ventana.setTitle("Academy - Eliminar cuenta");
        ventana.setScene(escena);
        ventana.show();

        ventana.setOnHidden(e -> usuarioServicio.cerrar());
    }

    private void cargarUsuarios() {

        List<Usuario> usuarios = usuarioServicio.listar();

        comboUsuarios.setItems(
                FXCollections.observableArrayList(usuarios)
        );
    }

    private void eliminarCuenta() {

        Usuario usuario;

        if (esAdministrador) {

            usuario = comboUsuarios.getValue();

            if (usuario == null) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Selecciona un usuario."
                );
                return;
            }

        } else {

            usuario = usuarioActual;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar cuenta");

        confirmacion.setContentText(
                "¿Seguro que quieres eliminar la cuenta de "
                + usuario.getNombre() + "?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            boolean resultado =
                    usuarioServicio.eliminar(usuario.getId());

            if (resultado) {

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Cuenta eliminada correctamente."
                );

                if (esAdministrador) {

                    cargarUsuarios();

                } else {

                    SesionUsuario.cerrarSesion();
                    ventana.close();
                    new VentanaLogin().mostrar();
                }

            } else {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "No se ha podido eliminar la cuenta."
                );
            }
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
