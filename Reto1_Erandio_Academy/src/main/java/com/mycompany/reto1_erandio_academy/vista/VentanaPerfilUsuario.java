
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.dao.UsuarioDAO;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaPerfilUsuario {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String FONDO = "#F2F5F7";
    private static final String GRIS = "#64748B";

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

        // =========================
        // PANEL PRINCIPAL
        // =========================

        VBox raiz = new VBox();
        raiz.setStyle(
                "-fx-background-color: " + FONDO + ";"
        );

        // =========================
        // CABECERA
        // =========================

        Label titulo = new Label("Mi perfil");

        titulo.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 25)
        );

        titulo.setTextFill(Color.web(TINTA));

        Label subtitulo = new Label(
                "Consulta y actualiza tus datos personales."
        );

        subtitulo.setFont(
                Font.font("Segoe UI", 13)
        );

        subtitulo.setTextFill(Color.web(GRIS));

        VBox textosCabecera = new VBox(
                5,
                titulo,
                subtitulo
        );

        String inicialTexto = usuarioActual.getNombre() == null
                || usuarioActual.getNombre().isEmpty()
                ? "U"
                : usuarioActual.getNombre()
                        .substring(0, 1)
                        .toUpperCase();

        Label inicial = new Label(inicialTexto);

        inicial.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 20)
        );

        inicial.setTextFill(Color.WHITE);
        inicial.setAlignment(Pos.CENTER);
        inicial.setMinSize(45, 45);
        inicial.setMaxSize(45, 45);

        inicial.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 50;"
        );

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox cabecera = new HBox(
                textosCabecera,
                espacio,
                inicial
        );

        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(
                new Insets(28, 35, 22, 35)
        );

        // =========================
        // TARJETA INFORMACIÓN
        // =========================

        Label tituloTarjeta = new Label(
                "Información personal"
        );

        tituloTarjeta.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 19)
        );

        tituloTarjeta.setTextFill(Color.web(TINTA));

        Label descripcionTarjeta = new Label(
                "Mantén tus datos actualizados para tu cuenta de Erandio Academy."
        );

        descripcionTarjeta.setFont(
                Font.font("Segoe UI", 12)
        );

        descripcionTarjeta.setTextFill(Color.web(GRIS));
        descripcionTarjeta.setWrapText(true);

        // =========================
        // CAMPO NOMBRE
        // =========================

        Label etiquetaNombre = crearEtiqueta("Nombre completo");

        campoNombre = crearCampo(
                "Introduce tu nombre"
        );

        campoNombre.setText(
                usuarioActual.getNombre()
        );

        // =========================
        // CAMPO EMAIL
        // =========================

        Label etiquetaEmail = crearEtiqueta(
                "Correo electrónico"
        );

        campoEmail = crearCampo(
                "Introduce tu correo electrónico"
        );

        campoEmail.setText(
                usuarioActual.getEmail()
        );

        // =========================
        // CAMPO TELÉFONO
        // =========================

        Label etiquetaTelefono = crearEtiqueta(
                "Teléfono"
        );

        campoTelefono = crearCampo(
                "Introduce tu teléfono"
        );

        campoTelefono.setText(
                usuarioActual.getTelefono()
        );

        // =========================
        // BOTÓN ACTUALIZAR
        // =========================

        Button botonActualizar = new Button(
                "Guardar cambios"
        );

        botonActualizar.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 13)
        );

        botonActualizar.setTextFill(Color.WHITE);
        botonActualizar.setPadding(
                new Insets(12, 25, 12, 25)
        );

        botonActualizar.setCursor(Cursor.HAND);

        botonActualizar.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-background-radius: 8;"
        );

        botonActualizar.setOnMouseEntered(e ->
                botonActualizar.setStyle(
                        "-fx-background-color: #18566D;"
                        + "-fx-background-radius: 8;"
                )
        );

        botonActualizar.setOnMouseExited(e ->
                botonActualizar.setStyle(
                        "-fx-background-color: " + AZUL + ";"
                        + "-fx-background-radius: 8;"
                )
        );

        botonActualizar.setOnAction(
                e -> actualizarPerfil()
        );

        HBox filaBoton = new HBox(
                botonActualizar
        );

        filaBoton.setAlignment(Pos.CENTER_RIGHT);

        // =========================
        // FORMULARIO
        // =========================

        VBox formulario = new VBox(
                9,
                etiquetaNombre,
                campoNombre,
                etiquetaEmail,
                campoEmail,
                etiquetaTelefono,
                campoTelefono
        );

        formulario.setSpacing(9);

        VBox tarjeta = new VBox(
                18,
                tituloTarjeta,
                descripcionTarjeta,
                formulario,
                filaBoton
        );

        tarjeta.setPadding(
                new Insets(28)
        );

        tarjeta.setMaxWidth(650);

        tarjeta.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #E1E8ED;"
                + "-fx-border-radius: 12;"
        );

        // =========================
        // CONTENIDO
        // =========================

        VBox contenido = new VBox(
                20,
                tarjeta
        );

        contenido.setAlignment(Pos.TOP_CENTER);

        contenido.setPadding(
                new Insets(15, 35, 35, 35)
        );

        // =========================
        // PIE DE VENTANA
        // =========================

        Label pie = new Label(
                "ERANDIO ACADEMY  |  Mi cuenta"
        );

        pie.setFont(
                Font.font("Segoe UI", 10)
        );

        pie.setTextFill(Color.web(GRIS));

        HBox pieContenedor = new HBox(pie);
        pieContenedor.setAlignment(Pos.CENTER);
        pieContenedor.setPadding(
                new Insets(10, 20, 18, 20)
        );

        raiz.getChildren().addAll(
                cabecera,
                contenido,
                pieContenedor
        );

        // =========================
        // MOSTRAR VENTANA
        // =========================

        Scene escena = new Scene(
                raiz,
                850,
                650
        );

        ventana.setTitle(
                "Erandio Academy - Mi perfil"
        );

        ventana.setMinWidth(650);
        ventana.setMinHeight(550);
        ventana.setScene(escena);

        ventana.setOnHidden(e -> usuarioDAO.cerrar());

        ventana.show();
    }

    // =========================
    // CREAR ETIQUETAS
    // =========================

    private Label crearEtiqueta(String texto) {

        Label etiqueta = new Label(texto);

        etiqueta.setFont(
                Font.font("Segoe UI", FontWeight.BOLD, 12)
        );

        etiqueta.setTextFill(Color.web(TINTA));

        return etiqueta;
    }

    // =========================
    // CREAR CAMPOS
    // =========================

    private TextField crearCampo(String texto) {

        TextField campo = new TextField();

        campo.setPromptText(texto);
        campo.setPrefHeight(42);
        campo.setMaxWidth(Double.MAX_VALUE);

        campo.setFont(
                Font.font("Segoe UI", 13)
        );

        campo.setStyle(
                "-fx-background-color: #F8FAFC;"
                + "-fx-border-color: #DCE3E9;"
                + "-fx-border-radius: 7;"
                + "-fx-background-radius: 7;"
                + "-fx-padding: 10;"
        );

        return campo;
    }

    // =========================
    // ACTUALIZAR PERFIL
    // =========================

    private void actualizarPerfil() {

        String nombre = campoNombre.getText().trim();
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty()
                || email.isEmpty()
                || telefono.isEmpty()) {

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

            boolean resultado = usuarioDAO.modificar(
                    usuarioActual
            );

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
                    "Error al actualizar el perfil: "
                    + ex.getMessage()
            );

        } finally {

            logServicio.cerrar();
        }
    }

    // =========================
    // MENSAJES
    // =========================

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle("Erandio Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
