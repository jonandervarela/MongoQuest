package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;
import com.mycompany.reto1_erandio_academy.servicio.SesionUsuario;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VentanaLogin {

    private static final Color TINTA = Color.web("#12283A");
    private static final Color AZUL = Color.web("#1F6F8B");
    private static final Color AMBAR = Color.web("#E8A33D");
    private static final Color ROJO = Color.web("#B3382C");
    private static final Color TEXTO_SUAVE = Color.web("#5B6B79");

    private final UsuarioServicio servicio = new UsuarioServicio();

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Iniciar sesión");
        ventana.setResizable(false);

        // ---------- Panel de marca (izquierda) ----------
        Label marca = new Label("Erandio\nAcademy");
        marca.setFont(fuente(FontWeight.BOLD, 40));
        marca.setTextFill(Color.WHITE);

        Region linea = new Region();
        pintar(linea, AMBAR, 0);
        linea.setMinSize(56, 4);
        linea.setPrefSize(56, 4);
        linea.setMaxSize(56, 4);

        Label lema = new Label(
                "Cursos, tareas y seguimiento del alumnado en un solo sitio."
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

        // ---------- Formulario (derecha) ----------
        Label titulo = new Label("Entra en tu cuenta");
        titulo.setFont(fuente(FontWeight.BOLD, 24));
        titulo.setTextFill(TINTA);

        Label ayuda = new Label("Usa el email con el que te registraste.");
        ayuda.setTextFill(TEXTO_SUAVE);

        Label etiquetaEmail = new Label("Email");
        etiquetaEmail.setFont(fuente(FontWeight.BOLD, 13));

        TextField email = new TextField();
        email.setPromptText("nombre@correo.com");

        Label etiquetaPassword = new Label("Contraseña");
        etiquetaPassword.setFont(fuente(FontWeight.BOLD, 13));

        PasswordField password = new PasswordField();
        password.setPromptText("Tu contraseña");

        Label error = new Label();
        error.setTextFill(ROJO);
        error.setPadding(new Insets(8, 12, 8, 12));
        pintar(error, Color.web("#FBEAE8"), 6);
        error.setWrapText(true);
        error.setMaxWidth(Double.MAX_VALUE);
        ocultarError(error);

        Button entrar = new Button("Entrar");
        estiloBoton(entrar);
        entrar.setFont(fuente(FontWeight.BOLD, 15));
        entrar.setPadding(new Insets(12, 18, 12, 18));
        entrar.setDefaultButton(true);
        entrar.setMaxWidth(Double.MAX_VALUE);

        Label textoRegistro = new Label("¿Primera vez aquí?");
        textoRegistro.setTextFill(TEXTO_SUAVE);

        Hyperlink crearCuenta = new Hyperlink("Crear una cuenta");
        crearCuenta.setTextFill(AZUL);
        crearCuenta.setOnAction(e -> new VentanaRegistroUsuario().mostrar());

        HBox pie = new HBox(4, textoRegistro, crearCuenta);
        pie.setAlignment(Pos.CENTER_LEFT);

        VBox panelFormulario = new VBox(
                18,
                new VBox(4, titulo, ayuda),
                new VBox(6, etiquetaEmail, email),
                new VBox(6, etiquetaPassword, password),
                error,
                entrar,
                pie
        );
        pintar(panelFormulario, Color.WHITE, 0);
        panelFormulario.setPadding(new Insets(40, 56, 40, 56));
        panelFormulario.setAlignment(Pos.CENTER_LEFT);
        panelFormulario.setMaxWidth(Double.MAX_VALUE);

        // ---------- Acción de entrar ----------
        entrar.setOnAction(e -> {

            ocultarError(error);

            try {

                Usuario usuario = servicio.iniciarSesion(
                        email.getText().trim(),
                        password.getText()
                );

                Runnable abrirMenu = menuSegunRol(usuario.getRol());

                if (abrirMenu == null) {
                    throw new IllegalStateException(
                            usuario.getRol() == null
                                    ? "El usuario no tiene un rol asignado."
                                    : "Rol no reconocido: " + usuario.getRol()
                    );
                }

                SesionUsuario.iniciarSesion(usuario);
                registrarLog("OK", null);

                // Primero se abre el menú y después se cierra el login
                abrirMenu.run();
                ventana.close();

            } catch (IllegalArgumentException | IllegalStateException ex) {

                // Credenciales incorrectas, rol inválido...
                SesionUsuario.cerrarSesion();
                registrarLog("ERROR", ex.getMessage());
                mostrarError(error, ex.getMessage());

            } catch (Exception ex) {

                // Otros fallos (por ejemplo, MongoDB apagado)
                SesionUsuario.cerrarSesion();
                ex.printStackTrace();
                mostrarError(
                        error,
                        "No se ha podido iniciar sesión. "
                        + "Comprueba que MongoDB está en marcha."
                );
            }
        });

        HBox raiz = new HBox(panelMarca, panelFormulario);
        HBox.setHgrow(panelFormulario, Priority.ALWAYS);

        ventana.setScene(new Scene(raiz, 840, 500));
        ventana.setOnHidden(e -> servicio.cerrar());
        ventana.show();
    }

    private Runnable menuSegunRol(String rol) {

        if (rol == null) {
            return null;
        }

        switch (rol.trim().toUpperCase()) {

            case "ADMIN":
                return () -> new VentanaMenuAdministrador().mostrar();

            case "EMPLEADO":
                return () -> new VentanaMenuEmpleado().mostrar();

            case "USUARIO":
                return () -> new VentanaMenuUsuario().mostrar();

            default:
                return null;
        }
    }

    private void registrarLog(String resultado, String detalleError) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar("LOGIN", "usuarios", resultado, detalleError);
        } finally {
            logServicio.cerrar();
        }
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

    // ---------- Estilos ----------

    private Font fuente(FontWeight peso, double tamanio) {
        return Font.font("Segoe UI", peso, tamanio);
    }

    private void pintar(Region region, Color color, double radio) {
        region.setBackground(new Background(new BackgroundFill(
                color, new CornerRadii(radio), Insets.EMPTY)));
    }

    // Botón azul que se oscurece al pasar el ratón
    private void estiloBoton(Button boton) {

        Color encima = AZUL.deriveColor(0, 1, 0.85, 1);

        pintar(boton, AZUL, 8);
        boton.setTextFill(Color.WHITE);
        boton.setCursor(Cursor.HAND);

        boton.setOnMouseEntered(e -> pintar(boton, encima, 8));
        boton.setOnMouseExited(e -> pintar(boton, AZUL, 8));
    }
}
