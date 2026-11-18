package com.mycompany.reto1_erandio_academy.vista;
import com.mycompany.reto1_erandio_academy.controlador.Gestor;

import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * "Registrar profesor" desde la lista de empleados del administrador.
 * Usa su propio Gestor/Gestor; al terminar, avisa a quien
 * la abrió (normalmente ListaEmpleadosController) a través de "alGuardar"
 * para que refresque su tabla.
 */
public class NuevoEmpleadoController implements Initializable {

    private final Gestor servicio = new Gestor();

    private Runnable alGuardar;

    @FXML private GridPane formulario;
    @FXML private TextField nombre;
    @FXML private TextField puesto;
    @FXML private TextField departamento;
    @FXML private TextField email;
    @FXML private TextField telefono;
    @FXML private PasswordField password;
    @FXML private CheckBox activo;
    @FXML private Button guardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // nada especial que inicializar
    }

    /** Lo llama la ventana que abre este formulario para saber cuándo recargar su tabla. */
    public void setAlGuardar(Runnable alGuardar) {
        this.alGuardar = alGuardar;
    }

    private void registrarLog(String operacion, String resultado, String error) {
        Gestor logServicio = new Gestor();
        try {
            logServicio.registrarLog(operacion, "empleados", resultado, error);
        } finally {
            logServicio.cerrar();
        }
    }

    @FXML
    private void onGuardar() {

        if (nombre.getText().isBlank()
                || puesto.getText().isBlank()
                || departamento.getText().isBlank()
                || email.getText().isBlank()
                || telefono.getText().isBlank()
                || password.getText().isBlank()) {

            mostrarMensaje(Alert.AlertType.WARNING, "Datos incompletos", "Debes rellenar todos los campos.");
            return;
        }

        if (!activo.isSelected()) {
            mostrarMensaje(Alert.AlertType.WARNING, "Empleado inactivo",
                    "Para crear una cuenta de profesor, el empleado debe estar activo.");
            return;
        }

        Empleado empleado = new Empleado(
                null,
                nombre.getText().trim(),
                puesto.getText().trim(),
                departamento.getText().trim(),
                true
        );

        boolean empleadoGuardado = false;

        Gestor usuarioServicio = new Gestor();

        try {

            servicio.registrarEmpleado(empleado);
            empleadoGuardado = true;

            Usuario usuario = new Usuario(
                    null,
                    nombre.getText().trim(),
                    email.getText().trim(),
                    password.getText(),
                    telefono.getText().trim()
            );

            usuario.setEmpleadoId(empleado.getId());

            usuarioServicio.registrarCuentaEmpleado(usuario);

            registrarLog("INSERT", "OK", null);

            if (alGuardar != null) {
                alGuardar.run();
            }

            mostrarMensaje(Alert.AlertType.INFORMATION, "Registro correcto",
                    "Profesor registrado correctamente.\n\n"
                    + "Nombre: " + empleado.getNombre()
                    + "\nCorreo: " + email.getText().trim()
                    + "\nRol: EMPLEADO");

            ((Stage) formulario.getScene().getWindow()).close();

        } catch (Exception ex) {

            if (empleadoGuardado) {
                try {
                    servicio.eliminarEmpleado(empleado.getId());
                } catch (Exception errorRollback) {
                    ex.addSuppressed(errorRollback);
                }
            }

            registrarLog("INSERT", "ERROR", ex.getMessage());

            mostrarMensaje(Alert.AlertType.ERROR, "Error al registrar profesor",
                    ex.getMessage() == null ? "Ha ocurrido un error inesperado." : ex.getMessage());

        } finally {
            usuarioServicio.cerrar();
            servicio.cerrar();
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
