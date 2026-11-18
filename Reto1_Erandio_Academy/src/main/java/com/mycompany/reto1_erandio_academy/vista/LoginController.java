package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    private static final String TINTA = "#12283A";
    private static final String AZUL = "#1F6F8B";
    private static final String AZUL_HOVER = "#2C87A6";
    private static final String AMBAR = "#E8A33D";
    private static final String ROJO = "#B3382C";
    private static final String TEXTO_SUAVE = "#5B6B79";

    private final Gestor servicio = new Gestor();

    @FXML private HBox raiz;
    @FXML private VBox panelMarca;
    @FXML private Label marca;
    @FXML private Region linea;
    @FXML private Label lema;
    @FXML private VBox panelFormulario;
    @FXML private Label titulo;
    @FXML private Label ayuda;
    @FXML private Label etiquetaEmail;
    @FXML private TextField email;
    @FXML private Label etiquetaPassword;
    @FXML private PasswordField password;
    @FXML private Label error;
    @FXML private Button entrar;
    @FXML private Label textoRegistro;
    @FXML private Hyperlink crearCuenta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarEstilos();
    }

    private void inicializarEstilos() {
        panelMarca.setStyle("-fx-background-color: " + TINTA + ";");
        marca.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;");
        linea.setStyle("-fx-background-color: " + AMBAR + ";");
        lema.setStyle("-fx-font-size: 14px; -fx-text-fill: #B9CAD6;");

        panelFormulario.setStyle("-fx-background-color: white;");
        titulo.setStyle("-fx-font-size: 23px; -fx-font-weight: bold; -fx-text-fill: " + TINTA + ";");
        ayuda.setStyle("-fx-text-fill: " + TEXTO_SUAVE + ";");
        etiquetaEmail.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        etiquetaPassword.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        textoRegistro.setStyle("-fx-text-fill: " + TEXTO_SUAVE + ";");
        crearCuenta.setStyle("-fx-text-fill: " + AZUL + ";");

        error.setStyle(
                "-fx-text-fill: " + ROJO + ";"
                + "-fx-background-color: #FBEAE8;"
                + "-fx-background-radius: 6;"
        );

        estiloBoton(entrar);
    }

    private void estiloBoton(Button boton) {
        boton.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        );
        boton.setOnMouseEntered(e -> boton.setStyle(
                "-fx-background-color: " + AZUL_HOVER + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        ));
        boton.setOnMouseExited(e -> boton.setStyle(
                "-fx-background-color: " + AZUL + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-cursor: hand;"
        ));
    }

    @FXML
    private void onEntrar() {

        ocultarError();

        try {

            Usuario usuario = servicio.iniciarSesion(
                    email.getText().trim(),
                    password.getText()
            );

            String rutaFxml = fxmlSegunRol(usuario.getRol());

            if (rutaFxml == null) {
                throw new IllegalStateException(
                        usuario.getRol() == null
                                ? "El usuario no tiene un rol asignado."
                                : "Rol no reconocido: " + usuario.getRol()
                );
            }

            SesionUsuario.iniciarSesion(usuario);
            registrarLog("OK", null);

            abrirFxml(rutaFxml);

        } catch (IllegalArgumentException | IllegalStateException ex) {

            SesionUsuario.cerrarSesion();
            registrarLog("ERROR", ex.getMessage());
            mostrarError(ex.getMessage());

        } catch (Exception ex) {

            SesionUsuario.cerrarSesion();
            ex.printStackTrace();
            mostrarError("No se ha podido iniciar sesión. Comprueba que MongoDB está en marcha.");
        }
    }

    @FXML
    private void onCrearCuenta() {
        com.mycompany.reto1_erandio_academy.controlador.Navegador.abrir(
                "/com/mycompany/reto1_erandio_academy/vista/RegistroUsuario.fxml");
    }

    private String fxmlSegunRol(String rol) {

        if (rol == null) {
            return null;
        }

        switch (rol.trim().toUpperCase()) {
            case "ADMIN":
                return "/com/mycompany/reto1_erandio_academy/vista/MenuAdministrador.fxml";
            case "EMPLEADO":
                return "/com/mycompany/reto1_erandio_academy/vista/MenuEmpleado.fxml";
            case "USUARIO":
                return "/com/mycompany/reto1_erandio_academy/vista/MenuUsuario.fxml";
            default:
                return null;
        }
    }

    private void abrirFxml(String ruta) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource(ruta));
            Parent raizNueva = cargador.load();

            Stage nuevaVentana = new Stage();
            nuevaVentana.setScene(new Scene(raizNueva));
            nuevaVentana.setTitle("Erandio Academy");
            nuevaVentana.show();

            // Cerramos la ventana de login
            Stage actual = (Stage) raiz.getScene().getWindow();
            actual.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarError("No se ha podido abrir el menú.");
        }
    }

    private void registrarLog(String resultado, String detalleError) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog("LOGIN", "usuarios", resultado, detalleError);
        } finally {
            logServicio.cerrar();
        }
    }

    private void mostrarError(String mensaje) {
        error.setText(mensaje);
        error.setManaged(true);
        error.setVisible(true);
    }

    private void ocultarError() {
        error.setText("");
        error.setManaged(false);
        error.setVisible(false);
    }
}
