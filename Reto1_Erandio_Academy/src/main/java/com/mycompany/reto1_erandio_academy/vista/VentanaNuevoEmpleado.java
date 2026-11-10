
package com.mycompany.reto1_erandio_academy.vista;

import com.mycompany.reto1_erandio_academy.modelo.Empleado;
import com.mycompany.reto1_erandio_academy.modelo.Usuario;
import com.mycompany.reto1_erandio_academy.servicio.EmpleadoServicio;
import com.mycompany.reto1_erandio_academy.servicio.UsuarioServicio;
import com.mycompany.reto1_erandio_academy.servicio.LogServicio;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VentanaNuevoEmpleado {

    private final EmpleadoServicio servicio;
    private final TableView<Empleado> tabla;

    public VentanaNuevoEmpleado(
            EmpleadoServicio servicio,
            TableView<Empleado> tabla) {

        this.servicio = servicio;
        this.tabla = tabla;
    }

    // Registrar operación en MongoDB
    private void registrarLog(
            String operacion,
            String resultado,
            String error) {

        LogServicio logServicio = new LogServicio();

        try {
            logServicio.registrar(
                    operacion,
                    "empleados",
                    resultado,
                    error
            );
        } finally {
            logServicio.cerrar();
        }
    }

    public void mostrar() {

        Stage ventana = new Stage();
        ventana.setTitle("Academy - Registrar profesor");

        // Campos del formulario
        TextField nombre = new TextField();
        nombre.setPromptText("Nombre completo");

        TextField puesto = new TextField();
        puesto.setPromptText("Puesto");

        TextField departamento = new TextField();
        departamento.setPromptText("Departamento");

        TextField email = new TextField();
        email.setPromptText("Correo electrónico");

        TextField telefono = new TextField();
        telefono.setPromptText("Número de teléfono");

        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña (mínimo 6 caracteres)");

        CheckBox activo = new CheckBox("Empleado activo");
        activo.setSelected(true);

        Button guardar = new Button("Guardar profesor");

        // GUARDAR PROFESOR
        guardar.setOnAction(e -> {

            // Validar campos obligatorios
            if (nombre.getText().isBlank()
                    || puesto.getText().isBlank()
                    || departamento.getText().isBlank()
                    || email.getText().isBlank()
                    || telefono.getText().isBlank()
                    || password.getText().isBlank()) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Datos incompletos",
                        "Debes rellenar todos los campos."
                );
                return;
            }

            if (!activo.isSelected()) {
                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Empleado inactivo",
                        "Para crear una cuenta de profesor, "
                        + "el empleado debe estar activo."
                );
                return;
            }

            // Crear empleado
            Empleado empleado = new Empleado(
                    null,
                    nombre.getText().trim(),
                    puesto.getText().trim(),
                    departamento.getText().trim(),
                    true
            );

            boolean empleadoGuardado = false;

            UsuarioServicio usuarioServicio = new UsuarioServicio();

            try {

                // 1. Guardar ficha del empleado
                servicio.registrar(empleado);
                empleadoGuardado = true;

                // 2. Crear cuenta del profesor
                Usuario usuario = new Usuario(
                        null,
                        nombre.getText().trim(),
                        email.getText().trim(),
                        password.getText(),
                        telefono.getText().trim()
                );

                // Vincular cuenta con la ficha del empleado
                usuario.setEmpleadoId(empleado.getId());

                // 3. Registrar cuenta con rol EMPLEADO
                usuarioServicio.registrarEmpleado(usuario);

                // 4. Registrar operación
                registrarLog("INSERT", "OK", null);

                // 5. Actualizar tabla
                tabla.setItems(
                        FXCollections.observableArrayList(
                                servicio.listar()
                        )
                );

                // 6. Mostrar confirmación
                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Registro correcto",
                        "Profesor registrado correctamente.\n\n"
                        + "Nombre: " + empleado.getNombre()
                        + "\nCorreo: " + email.getText().trim()
                        + "\nRol: EMPLEADO"
                );

                ventana.close();

            } catch (Exception ex) {

                // Si falla la cuenta, intentar eliminar la ficha
                if (empleadoGuardado) {
                    try {
                        servicio.eliminar(empleado.getId());
                    } catch (Exception errorRollback) {
                        ex.addSuppressed(errorRollback);
                    }
                }

                registrarLog(
                        "INSERT",
                        "ERROR",
                        ex.getMessage()
                );

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Error al registrar profesor",
                        ex.getMessage() == null
                        ? "Ha ocurrido un error inesperado."
                        : ex.getMessage()
                );

            } finally {
                usuarioServicio.cerrar();
            }
        });

        // Organizar formulario
        GridPane formulario = new GridPane();

        formulario.setPadding(new Insets(20));
        formulario.setHgap(12);
        formulario.setVgap(12);

        formulario.add(new Label("Nombre:"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Puesto:"), 0, 1);
        formulario.add(puesto, 1, 1);

        formulario.add(new Label("Departamento:"), 0, 2);
        formulario.add(departamento, 1, 2);

        formulario.add(new Label("Correo:"), 0, 3);
        formulario.add(email, 1, 3);

        formulario.add(new Label("Teléfono:"), 0, 4);
        formulario.add(telefono, 1, 4);

        formulario.add(new Label("Contraseña:"), 0, 5);
        formulario.add(password, 1, 5);

        formulario.add(activo, 1, 6);
        formulario.add(guardar, 1, 7);

        ventana.setScene(
                new Scene(formulario, 500, 420)
        );

        ventana.setResizable(false);
        ventana.show();
    }

    // Mostrar mensajes
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle("Academy");
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
