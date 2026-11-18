package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.DAOImpl;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * "Consultar perfil" del actor Empleado (profesor): sus datos de cuenta
 * (editables) más los datos de su ficha de empleado (solo consulta).
 */
public class PerfilEmpleadoController implements Initializable {

    private DAOImpl usuarioDAO;
    private DAOImpl empleadoDAO;
    private Usuario usuarioActual;

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private Label etiquetaNombre;
    @FXML private TextField campoNombre;
    @FXML private Label etiquetaEmail;
    @FXML private TextField campoEmail;
    @FXML private Label etiquetaTelefono;
    @FXML private TextField campoTelefono;
    @FXML private Button botonActualizar;
    @FXML private VBox bloqueEmpleado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        contenido.setStyle("-fx-background-color: #F2F5F7;");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            mostrarMensaje(Alert.AlertType.WARNING, "No hay ningún usuario conectado.");
            return;
        }

        usuarioDAO = new DAOImpl();
        empleadoDAO = new DAOImpl();

        campoNombre.setText(usuarioActual.getNombre());
        campoEmail.setText(usuarioActual.getEmail());
        campoTelefono.setText(usuarioActual.getTelefono());

        estiloBoton(botonActualizar);

        construirBloqueEmpleado();

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> cerrarDaos());
                    }
                });
            }
        });
    }

    private void cerrarDaos() {
        if (usuarioDAO != null) {
            usuarioDAO.cerrar();
        }
        if (empleadoDAO != null) {
            empleadoDAO.cerrar();
        }
    }

    private void construirBloqueEmpleado() {

        Label subtitulo = new Label("Datos como profesor");
        subtitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        if (usuarioActual.getEmpleadoId() == null) {
            Label aviso = new Label(
                    "Tu cuenta no está vinculada a ninguna ficha de "
                    + "empleado. Pide al administrador que la enlace.");
            aviso.setWrapText(true);
            bloqueEmpleado.getChildren().addAll(subtitulo, aviso);
            return;
        }

        Empleado empleado = empleadoDAO.buscarEmpleadoPorId(usuarioActual.getEmpleadoId());

        if (empleado == null) {
            Label aviso = new Label("No se ha encontrado la ficha de empleado asociada.");
            aviso.setWrapText(true);
            bloqueEmpleado.getChildren().addAll(subtitulo, aviso);
            return;
        }

        Label puesto = new Label("Puesto: " + empleado.getPuesto());
        Label departamento = new Label("Departamento: " + empleado.getDepartamento());
        Label estado = new Label("Estado: " + (empleado.isActivo() ? "Activo" : "Inactivo"));

        Label nota = new Label("Estos datos los gestiona el administrador.");
        nota.setStyle("-fx-font-size: 11px; -fx-text-fill: #5B6B79;");

        bloqueEmpleado.getChildren().addAll(subtitulo, puesto, departamento, estado, nota);
    }

    @FXML
    private void onActualizar() {

        String nombre = campoNombre.getText().trim();
        String email = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Todos los campos son obligatorios.");
            return;
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setEmail(email);
        usuarioActual.setTelefono(telefono);

        Gestor logServicio = new Gestor();

        try {
            boolean resultado = usuarioDAO.modificarUsuario(usuarioActual);

            if (resultado) {
                logServicio.registrarLog("UPDATE", "usuarios", "OK", null);
                mostrarMensaje(Alert.AlertType.INFORMATION, "Tu perfil se ha actualizado correctamente.");
            } else {
                logServicio.registrarLog("UPDATE", "usuarios", "ERROR", "No se pudo actualizar el perfil.");
                mostrarMensaje(Alert.AlertType.WARNING, "No se ha podido actualizar el perfil.");
            }

        } catch (Exception ex) {
            logServicio.registrarLog("UPDATE", "usuarios", "ERROR", ex.getMessage());
            mostrarMensaje(Alert.AlertType.ERROR, "Error al actualizar el perfil: " + ex.getMessage());
        } finally {
            logServicio.cerrar();
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void estiloBoton(Button boton) {

        String base =
                "-fx-background-color: white;"
                + "-fx-text-fill: #12283A;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-color: #D5DDE3;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        String encima =
                "-fx-background-color: #1F6F8B;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
                + "-fx-border-radius: 8;"
                + "-fx-cursor: hand;"
                + "-fx-padding: 8 16 8 16;";

        boton.setStyle(base);
        boton.setOnMouseEntered(e -> boton.setStyle(encima));
        boton.setOnMouseExited(e -> boton.setStyle(base));
    }
}
