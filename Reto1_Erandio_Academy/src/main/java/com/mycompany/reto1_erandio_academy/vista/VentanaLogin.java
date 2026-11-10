
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VentanaLogin {

    private final UsuarioServicio servicio = new UsuarioServicio();

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Iniciar sesión");

        Label titulo = new Label("INICIAR SESIÓN");
        titulo.setStyle(
                "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        TextField email = new TextField();
        email.setPromptText("Introduce tu email");

        PasswordField password = new PasswordField();
        password.setPromptText("Introduce tu contraseña");

        Button entrar = new Button("Entrar");

        entrar.setOnAction(e -> {

            try {

                Usuario usuario = servicio.iniciarSesion(
                        email.getText().trim(),
                        password.getText()
                );

                // Guardar la sesión del usuario
                SesionUsuario.iniciarSesion(usuario);
                LogServicio logServicio = new LogServicio();

logServicio.registrar(
        "LOGIN",
        "usuarios",
        "OK",
        null
);

logServicio.cerrar();


                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Academy");
                alerta.setHeaderText("Inicio de sesión correcto");
                alerta.setContentText(
                        "Bienvenido/a, " + usuario.getNombre()
                        + "\nRol: " + usuario.getRol()
                );
                alerta.showAndWait();

                ventana.close();

                // Abrir menú según el rol
                String rol = usuario.getRol();

                if (rol == null) {
                    SesionUsuario.cerrarSesion();
                    mostrarError("El usuario no tiene un rol asignado.");
                    return;
                }

                switch (rol.trim().toUpperCase()) {

                    case "ADMIN":
                        new VentanaMenuAdministrador().mostrar();
                        break;

                    case "EMPLEADO":
                        new VentanaMenuEmpleado().mostrar();
                        break;

                    case "USUARIO":
                        new VentanaMenuUsuario().mostrar();
                        break;

                    default:
                        SesionUsuario.cerrarSesion();
                        mostrarError("Rol no reconocido: " + rol);
                        break;
                }

            } catch (Exception ex) {

                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Error de acceso");
                alerta.setHeaderText("No se ha podido iniciar sesión");
                alerta.setContentText(ex.getMessage());
                alerta.showAndWait();
            }
        });

        VBox contenido = new VBox(
                20,
                titulo,
                new Label("Email:"),
                email,
                new Label("Contraseña:"),
                password,
                entrar
        );

        contenido.setAlignment(Pos.CENTER);
        contenido.setPadding(new Insets(30));

        ventana.setScene(new Scene(contenido, 400, 400));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    private void mostrarError(String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Academy");
        alerta.setHeaderText("Error de acceso");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
