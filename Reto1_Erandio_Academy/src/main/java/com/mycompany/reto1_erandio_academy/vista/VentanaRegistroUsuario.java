
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaRegistroUsuario {

    // COLORES DE ACADEMY
    private static final Color TINTA = Color.web("#12283A");
    private static final Color AZUL = Color.web("#1F6F8B");
    private static final Color AMBAR = Color.web("#E8A33D");
    private static final Color ROJO = Color.web("#B3382C");
    private static final Color TEXTO_SUAVE = Color.web("#5B6B79");

    private final UsuarioServicio servicio = new UsuarioServicio();

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Crear cuenta");
        ventana.setResizable(false);

        // ---------- PANEL IZQUIERDO ----------

        Label marca = new Label("Erandio\nAcademy");
        marca.setFont(fuente(FontWeight.BOLD, 40));
        marca.setTextFill(Color.WHITE);

        Region linea = new Region();
        pintar(linea, AMBAR, 0);
        linea.setMinSize(56, 4);
        linea.setPrefSize(56, 4);
        linea.setMaxSize(56, 4);

        Label lema = new Label(
                "Únete a nuestra comunidad y "
                + "comienza tu formación con Academy."
        );

        lema.setFont(fuente(FontWeight.NORMAL, 15));
        lema.setTextFill(Color.web("#B9CAD6"));
        lema.setWrapText(true);

        VBox panelMarca = new VBox(18, marca, linea, lema);
        pintar(panelMarca, TINTA, 0);
        panelMarca.setPadding(new Insets(48));
        panelMarca.setAlignment(Pos.CENTER_LEFT);
        panelMarca.setMinWidth(340);
        panelMarca.setPrefWidth(340);

        // ---------- FORMULARIO ----------

        Label titulo = new Label("Crear una cuenta");
        titulo.setFont(fuente(FontWeight.BOLD, 24));
        titulo.setTextFill(TINTA);

        Label ayuda = new Label(
                "Introduce tus datos para registrarte en Academy."
        );
        ayuda.setTextFill(TEXTO_SUAVE);

        // Nombre
        Label etiquetaNombre = new Label("Nombre");
        etiquetaNombre.setFont(fuente(FontWeight.BOLD, 13));

        TextField nombre = new TextField();
        nombre.setPromptText("Introduce tu nombre");

        // Email
        Label etiquetaEmail = new Label("Email");
        etiquetaEmail.setFont(fuente(FontWeight.BOLD, 13));

        TextField email = new TextField();
        email.setPromptText("nombre@correo.com");

        // Contraseña
        Label etiquetaPassword = new Label("Contraseña");
        etiquetaPassword.setFont(fuente(FontWeight.BOLD, 13));

        PasswordField password = new PasswordField();
        password.setPromptText("Mínimo 6 caracteres");

        // Teléfono
        Label etiquetaTelefono = new Label("Teléfono");
        etiquetaTelefono.setFont(fuente(FontWeight.BOLD, 13));

        TextField telefono = new TextField();
        telefono.setPromptText("Introduce tu teléfono");

        // Mensaje de error
        Label error = new Label();
        error.setTextFill(ROJO);
        error.setPadding(new Insets(8, 12, 8, 12));
        pintar(error, Color.web("#FBEAE8"), 6);
        error.setWrapText(true);
        error.setMaxWidth(Double.MAX_VALUE);
        ocultarError(error);

        // Botón registrar
        Button registrar = new Button("Crear cuenta");
        estiloBoton(registrar);
        registrar.setFont(fuente(FontWeight.BOLD, 15));
        registrar.setPadding(new Insets(12, 18, 12, 18));
        registrar.setDefaultButton(true);
        registrar.setMaxWidth(Double.MAX_VALUE);

        // Panel del formulario
        VBox panelFormulario = new VBox(
                12,
                new VBox(4, titulo, ayuda),
                new VBox(5, etiquetaNombre, nombre),
                new VBox(5, etiquetaEmail, email),
                new VBox(5, etiquetaPassword, password),
                new VBox(5, etiquetaTelefono, telefono),
                error,
                registrar
        );

        pintar(panelFormulario, Color.WHITE, 0);
        panelFormulario.setPadding(new Insets(28, 56, 28, 56));
        panelFormulario.setAlignment(Pos.CENTER_LEFT);
        panelFormulario.setMaxWidth(Double.MAX_VALUE);

        // ---------- ACCIÓN REGISTRAR ----------

        registrar.setOnAction(e -> {

            ocultarError(error);

            try {

                Usuario usuario = new Usuario(
                        null,
                        nombre.getText().trim(),
                        email.getText().trim(),
                        password.getText(),
                        telefono.getText().trim()
                );

                servicio.registrar(usuario);

                registrarLog("INSERT", "OK", null);

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Usuario registrado correctamente."
                );

                // Limpiar campos
                nombre.clear();
                email.clear();
                password.clear();
                telefono.clear();

            } catch (Exception ex) {

                registrarLog("INSERT", "ERROR", ex.getMessage());

                mostrarError(
                        error,
                        ex.getMessage()
                );
            }
        });

        // ---------- VENTANA PRINCIPAL ----------

        HBox raiz = new HBox(panelMarca, panelFormulario);

        HBox.setHgrow(panelFormulario, Priority.ALWAYS);

        ventana.setScene(new Scene(raiz, 840, 560));

        ventana.setOnHidden(e -> servicio.cerrar());

        ventana.show();
    }

    // ---------- REGISTRAR LOG ----------

    private void registrarLog(
            String operacion,
            String resultado,
            String error
    ) {

        LogServicio logServicio = new LogServicio();

        try {

            logServicio.registrar(
                    operacion,
                    "usuarios",
                    resultado,
                    error
            );

        } finally {
            logServicio.cerrar();
        }
    }

    // ---------- MENSAJES ----------

    private void mostrarMensaje(
            Alert.AlertType tipo,
            String mensaje
    ) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(Label etiqueta, String mensaje) {

        etiqueta.setText(mensaje);
        etiqueta.setManaged(true);
        etiqueta.setVisible(true);
    }

    private void ocultarError(Label etiqueta) {

        etiqueta.setText("");
        etiqueta.setManaged(false);
        etiqueta.setVisible(false);
    }

    // ---------- ESTILOS ----------

    private Font fuente(FontWeight peso, double tamanio) {

        return Font.font("Segoe UI", peso, tamanio);
    }

    private void pintar(
            Region region,
            Color color,
            double radio
    ) {

        region.setBackground(new Background(
                new BackgroundFill(
                        color,
                        new CornerRadii(radio),
                        Insets.EMPTY
                )
        ));
    }

    private void estiloBoton(Button boton) {

        Color encima = AZUL.deriveColor(0, 1, 0.85, 1);

        pintar(boton, AZUL, 8);

        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);

        boton.setOnMouseEntered(e ->
                pintar(boton, encima, 8)
        );

        boton.setOnMouseExited(e ->
                pintar(boton, AZUL, 8)
        );
    }
}
