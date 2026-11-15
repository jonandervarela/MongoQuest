
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class VentanaEliminarCuenta {

    private static final String AZUL_OSCURO = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String ROJO = "#DC3545";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

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

        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: " + FONDO + ";");

        // CABECERA
        VBox cabecera = new VBox(6);
        cabecera.setPadding(new Insets(25, 30, 25, 30));
        cabecera.setStyle(
                "-fx-background-color: " + AZUL_OSCURO + ";"
        );

        Label titulo = new Label("Eliminar cuenta");
        titulo.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
        );

        Label subtitulo = new Label(
                esAdministrador
                        ? "Administración de cuentas de usuario"
                        : "Eliminación de tu cuenta personal"
        );
        subtitulo.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #CBD5E1;"
        );

        cabecera.getChildren().addAll(titulo, subtitulo);
        raiz.setTop(cabecera);

        // CONTENIDO
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(30));
        contenido.setMaxWidth(750);

        Label tituloSeccion = new Label(
                esAdministrador
                        ? "Seleccionar cuenta"
                        : "Información de tu cuenta"
        );
        tituloSeccion.setStyle(
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";"
        );

        Label descripcion = new Label(
                esAdministrador
                        ? "Selecciona el usuario cuya cuenta deseas eliminar."
                        : "Estás a punto de eliminar permanentemente tu cuenta."
        );
        descripcion.setWrapText(true);
        descripcion.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " + GRIS + ";"
        );

        VBox tarjeta = new VBox(18);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12;"
        );

        tarjeta.getChildren().addAll(tituloSeccion, descripcion);

        if (esAdministrador) {

            Label etiqueta = new Label("Usuario");
            etiqueta.setStyle(
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: " + AZUL_OSCURO + ";"
            );

            comboUsuarios = new ComboBox<>();
            comboUsuarios.setMaxWidth(Double.MAX_VALUE);
            comboUsuarios.setPrefHeight(42);
            comboUsuarios.setPromptText("Seleccionar usuario");

            comboUsuarios.setConverter(new StringConverter<Usuario>() {

                @Override
                public String toString(Usuario usuario) {
                    return usuario == null
                            ? ""
                            : usuario.getNombre()
                            + " (" + usuario.getEmail() + ")";
                }

                @Override
                public Usuario fromString(String texto) {
                    return null;
                }
            });

            cargarUsuarios();

            tarjeta.getChildren().addAll(etiqueta, comboUsuarios);

        } else {

            Label etiquetaNombre = crearEtiqueta("Nombre");
            Label nombre = crearDato(usuarioActual.getNombre());

            Label etiquetaEmail = crearEtiqueta("Correo electrónico");
            Label email = crearDato(usuarioActual.getEmail());

            VBox datos = new VBox(8);
            datos.getChildren().addAll(
                    etiquetaNombre,
                    nombre,
                    etiquetaEmail,
                    email
            );

            tarjeta.getChildren().add(datos);
        }

        // AVISO
        VBox aviso = new VBox(8);
        aviso.setPadding(new Insets(18));
        aviso.setStyle(
                "-fx-background-color: #FFF1F2;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #FECDD3;" +
                "-fx-border-radius: 10;"
        );

        Label tituloAviso = new Label("¡Atención! Esta acción es irreversible");
        tituloAviso.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + ROJO + ";"
        );

        Label textoAviso = new Label(
                "Al eliminar la cuenta, dejarás de tener acceso a ella. " +
                "Comprueba que has seleccionado el usuario correcto antes de continuar."
        );
        textoAviso.setWrapText(true);
        textoAviso.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #7F1D1D;"
        );

        aviso.getChildren().addAll(tituloAviso, textoAviso);

        // BOTONES
        Button botonEliminar = new Button("Eliminar cuenta");
        botonEliminar.setPrefHeight(42);
        botonEliminar.setStyle(
                "-fx-background-color: " + ROJO + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 22;" +
                "-fx-cursor: hand;"
        );

        botonEliminar.setOnAction(e -> eliminarCuenta());

        HBox zonaBoton = new HBox(botonEliminar);
        zonaBoton.setAlignment(Pos.CENTER_RIGHT);

        contenido.getChildren().addAll(
                tituloSeccion,
                tarjeta,
                aviso,
                zonaBoton
        );

        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + FONDO + ";" +
                "-fx-background-color: " + FONDO + ";" +
                "-fx-border-color: transparent;"
        );

        raiz.setCenter(scroll);

        Scene escena = new Scene(raiz, 750, 600);

        ventana.setTitle("Erandio Academy - Eliminar cuenta");
        ventana.setScene(escena);
        ventana.setMinWidth(550);
        ventana.setMinHeight(450);

        ventana.setOnHidden(e -> usuarioServicio.cerrar());

        ventana.show();
    }

    private Label crearEtiqueta(String texto) {

        Label label = new Label(texto);
        label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + GRIS + ";"
        );

        return label;
    }

    private Label crearDato(String texto) {

        Label label = new Label(texto == null ? "" : texto);
        label.setWrapText(true);
        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + AZUL_OSCURO + ";" +
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 10;"
        );

        return label;
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
        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
