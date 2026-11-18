package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.controlador.SesionUsuario;
import com.mycompany.reto1_erandio_academy.modelo.Empresa;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class EmpresaController implements Initializable {

    private final Gestor empresaServicio = new Gestor();
    private Empresa empresaActual;

    @FXML private VBox contenido;
    @FXML private Label titulo;
    @FXML private GridPane formulario;
    @FXML private TextField nombre;
    @FXML private TextField sector;
    @FXML private TextField actividad;
    @FXML private TextField direccion;
    @FXML private TextArea descripcion;
    @FXML private TextField estado;
    @FXML private Button guardar;
    @FXML private Button cerrarBoton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        contenido.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((o2, oldW, newW) -> {
                    if (newW != null) {
                        newW.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> empresaServicio.cerrar());
                    }
                });
            }
        });

        if (!SesionUsuario.tieneRol("ADMIN")) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado",
                    "Solo el administrador puede gestionar la empresa.");
            deshabilitarTodo();
            return;
        }

        try {

            empresaActual = empresaServicio.obtenerEmpresa();

            if (empresaActual == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Empresa no encontrada",
                        "No existen datos de empresa en MongoDB.");
                deshabilitarTodo();
                return;
            }

            nombre.setText(empresaActual.getNombre());
            sector.setText(empresaActual.getSector());
            actividad.setText(empresaActual.getActividad());
            direccion.setText(empresaActual.getDireccion());
            descripcion.setText(empresaActual.getDescripcion());
            estado.setText(empresaActual.getEstado());

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", ex.getMessage());
            deshabilitarTodo();
        }
    }

    private void deshabilitarTodo() {
        formulario.setDisable(true);
        guardar.setDisable(true);
    }

    @FXML
    private void onGuardar() {

        try {
            if (nombre.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo obligatorio",
                        "El nombre de la empresa es obligatorio.");
                return;
            }

            empresaActual.setNombre(nombre.getText().trim());
            empresaActual.setSector(sector.getText().trim());
            empresaActual.setActividad(actividad.getText().trim());
            empresaActual.setDireccion(direccion.getText().trim());
            empresaActual.setDescripcion(descripcion.getText().trim());
            empresaActual.setEstado(estado.getText().trim());

            empresaServicio.actualizarEmpresa(empresaActual);

            Gestor logServicio = new Gestor();
            try {
                logServicio.registrarLog("UPDATE", "empresa", "OK", null);
            } finally {
                logServicio.cerrar();
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado correctamente",
                    "Los datos de Erandio Academy se han actualizado.");

        } catch (Exception ex) {

            Gestor logServicio = new Gestor();
            try {
                logServicio.registrarLog("UPDATE", "empresa", "ERROR", ex.getMessage());
            } finally {
                logServicio.cerrar();
            }

            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar", ex.getMessage());
        }
    }

    @FXML
    private void onCerrar() {
        ((Stage) contenido.getScene().getWindow()).close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje == null ? "Error desconocido" : mensaje);
        alerta.showAndWait();
    }
}
